package com.example.userservice.password;

import com.example.userservice.exception.ApiRequestException;
import com.example.userservice.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.ResponseHandler;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public PasswordService(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }


    public List<Password> getPasswords(){
        return passwordRepository.findAll();
    }

    public Password getPasswordById(Long id) {
        Optional<Password> pwById = passwordRepository.findPasswordById(id);
        if(!pwById.isPresent()) throw new ApiRequestException("There is no password with id: "+ id, HttpStatus.BAD_REQUEST);

        return pwById.get();
    }

    public Password updatePassword(Password password) {
        if(password.getPassword().isEmpty()) throw new ApiRequestException("Password is empty!", HttpStatus.BAD_REQUEST);

        ObjectMapper mapper = new ObjectMapper();
        try{
            ResponseEntity<String> validateMessage = restTemplate.getForEntity("http://features-service/checkPasswordHistory/strength?password="+password.getPassword(), String.class);
            JsonNode root = mapper.readTree(validateMessage.getBody());
            if(root.asText().equals("tooShort")) throw new ApiRequestException("Password is too short. It has less than 5 characters. Add more letters, numbers od special characters", HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            if(e.getMessage().equals("Password is too short. It has less than 5 characters. Add more letters, numbers od special characters"))
                throw new ApiRequestException("Password is too short. It has less than 5 characters. Add more letters, numbers od special characters", HttpStatus.BAD_REQUEST);
            throw new ApiRequestException("No servers available for service: features-service", HttpStatus.BAD_REQUEST);
        }


        Optional<Password> pass = passwordRepository.findPasswordById(password.getId())
                .map(pw ->{
                    pw.setPassword(password.getPassword());
                    pw.setCreatedAt(LocalDateTime.now());
                    return passwordRepository.save(pw);
                });
        if(pass.isPresent()) return pass.get();
        else throw new ApiRequestException("There is no password with id: "+ password.getId(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Object> checkPasswordExpirationAndNotifyUser() {
        List<Password> passwords = passwordRepository.findAll();
        List<String> emails = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        // get user emails with expired password
        for(Password pw:passwords){
            long mjeseci = ChronoUnit.MONTHS.between(pw.getCreatedAt(), LocalDateTime.now());
            if(mjeseci>=6){
                try{
                    ResponseEntity<String> email = restTemplate.getForEntity("http://user-service/user?passwordId="+pw.getId().toString(), String.class);
                    JsonNode root = mapper.readTree(email.getBody());
                    emails.add(root.path("email").asText());
                }
                catch (Exception e){
                    throw new ApiRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                //System.out.println("PRINTANJEE");
            }
        }

        if(emails.isEmpty()){
            HashMap<String, String> hash = new HashMap<>();
            hash.put("message","There are no users with expired password");
            return new ResponseEntity<>(hash, HttpStatus.OK);
        }

        String msg = "You should change your password because at least 6 months have passed since the last password change";
        //notify users with expired password
        List<HashMap<String,String>> mails = new ArrayList<>();
        for(String email:emails){
            try{
                ResponseEntity<String> notify = restTemplate.getForEntity("http://notification-service/passwordAdvices/expiration?email="+email+"&message="+msg, String.class);
                JsonNode root = mapper.readTree(notify.getBody());
                HashMap<String,String> json = new HashMap<>();
                json.put("email", email);
                json.put("StatusCodeValue", String.valueOf(notify.getStatusCodeValue()));
                mails.add(json);
            }
            catch (Exception e){
                throw new ApiRequestException("No servers available for service: notification-service", HttpStatus.BAD_REQUEST);
            }
        }
        List<HashMap<String,String>> errors = new ArrayList<>();
        //System.out.println(mails);
        boolean error = false;
        //check sending status
        for(HashMap<String,String> o:mails){
            //System.out.println(o.get("StatusCodeValue"));
            if(!o.get("StatusCodeValue").equals("200")){
                errors.add(o);
                error=true;
            }
        }
        if(error) throw new ApiRequestException(errors.toString(), HttpStatus.BAD_REQUEST);


        return new ResponseEntity<>(mails,HttpStatus.OK);

    }
}
