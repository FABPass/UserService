package com.example.userservice.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.userservice.exception.ApiRequestException;
import com.example.userservice.mfa.Mfa;
import com.example.userservice.password.Password;
import com.example.userservice.rabbitmq.Config;
import com.example.userservice.rabbitmq.Event;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email){
        Optional<User> userByEmail = userRepository.findUserByEmail(email);
        if(!userByEmail.isPresent()){
            throw new ApiRequestException("There is no user registered with email: "+ email, BAD_REQUEST);
        }
        return userByEmail.get();
    }

    public User getUserById(Long id){
        Optional<User> userById = userRepository.findUserById(id);
        if(!userById.isPresent()){
            throw new ApiRequestException("There is no user with id: "+ id, BAD_REQUEST);
        }
        return userById.get();
    }


    public ResponseEntity<Object> getUserEmailByPasswordId(Long passwordId) {
        Optional<User> userByPasswordId = userRepository.findUserByPasswordId(passwordId);
        if(!userByPasswordId.isPresent()){
            throw new ApiRequestException("There is no user with passwordId: "+ passwordId, BAD_REQUEST);
        }
        JSONObject email = new JSONObject();
        email.put("email", userByPasswordId.get().getEmail());
        return ResponseEntity.status(OK).body(email.toString());
    }

    public User createNewUser(User user, String password) {
        addOrUpdateUserValidation(user);
        if(password.isEmpty()) throw new ApiRequestException("Password is empty!", BAD_REQUEST);

        Password pw = new Password(passwordEncoder.encode(password), LocalDateTime.now());
        Mfa mfa = new Mfa("None","",false);
        user.setPassword(pw);
        user.setMfa(mfa);
        try{
            User save = userRepository.save(user);
            Event message = new Event(save.getEmail(), "Welcome "+save.getName()+" "+save.getSurname()+".\n"+"Successfully registred. You can use FABPass now");
            rabbitTemplate.convertAndSend(Config.USER_SERVICE_EXCHANGE, Config.USER_SERVICE_RK, message);
            return save;
        }
        catch (Exception e){
            throw new ApiRequestException("You can not add user with existing email or phone", BAD_REQUEST);
        }
        // exception kad ima isti email ili isti pw i poruka o uspjehu
        //return user;
    }

    public User updateUser(User user){
        addOrUpdateUserValidation(user);
        Optional<User> found = userRepository.findUserById(user.getId())
                .map(u -> {
                    u.setName(user.getName());
                    u.setSurname(user.getSurname());
                    u.setEmail(user.getEmail());
                    u.setPhone(user.getPhone());
                    return userRepository.save(u);
                });
        if(found.isPresent()) return found.get();
        else throw new ApiRequestException("There is no user with id: "+ user.getId(), BAD_REQUEST);

    }


    public void deleteUserById(Long id) {
        Optional<User> userById = userRepository.findUserById(id);
        if(userById.isPresent()) userRepository.deleteById(id);
        else throw new ApiRequestException("There is no user with id: "+ id, BAD_REQUEST);
    }

    public void deleteUserByEmail(String email) {
        Optional<User> userById = userRepository.findUserByEmail(email);
        if(userById.isPresent()) userRepository.deleteById(userById.get().getId());
        else throw new ApiRequestException("There is no user registered with email: "+ email, BAD_REQUEST);
    }

    public void addOrUpdateUserValidation(User user){
        if(user.getName().isEmpty()) throw new ApiRequestException("Name is empty!", BAD_REQUEST);
        if(user.getSurname().isEmpty()) throw new ApiRequestException("Surname is empty!", BAD_REQUEST);
        if(user.getEmail().isEmpty()) throw new ApiRequestException("Email is empty!", BAD_REQUEST);
        if(user.getPhone().isEmpty()) throw new ApiRequestException("Phone is empty!", BAD_REQUEST);
        String regexPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(!Pattern.compile(regexPattern).matcher(user.getEmail()).matches()) throw new ApiRequestException("Email format is not valid. Please provide valid email", BAD_REQUEST);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email);
        if(!user.isPresent()){
            throw new UsernameNotFoundException("User not found in the database");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword().getPassword(),authorities);
    }


    public ResponseEntity<Object> refreshToken(HttpHeaders headers) {
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("promijeniSecretToken12312213sadadse12312312312312312b12312");
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String email = decodedJWT.getSubject();
                Optional<User> user = userRepository.findUserByEmail(email);

                if(!user.isPresent()) throw new UsernameNotFoundException("User not found in the database");

                List<String> authorities = new ArrayList<>();
                authorities.add("ROLE_USER");

                String access_token = JWT.create()
                        .withSubject(user.get().getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(headers.getFirst(HttpHeaders.HOST))
                        .withClaim("roles", authorities)
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token",access_token);
                tokens.put("refresh_token",refresh_token);
                return new ResponseEntity<>(tokens, OK);

            }catch(Exception exception){
                HashMap<String, String> hash = new HashMap<>();
                hash.put("error",exception.getMessage());
                return new ResponseEntity<>(hash, FORBIDDEN);
            }
        }else{
            throw new RuntimeException("Refresh token is missing");
        }
    }

    public ResponseEntity<Object> sendForgotPasswordEmail(String email) {
        try{
            Optional<User> user = userRepository.findUserByEmail(email);
            if(!user.isPresent()) throw new ApiRequestException("User not found", NOT_FOUND);
            String token = createToken(user.get(),15);

            String msg = "If you want to reset your password click on following link :  http://localhost:3000/changePassword/"+token+"\n";
            msg += "You have 15 minutes to reset password";
            ResponseEntity<String> sendEmail = restTemplate.getForEntity("http://notification-service/passwordAdvices/expiration?email="+email+"&message="+msg, String.class);


            if(sendEmail.getStatusCodeValue() == 200){
                Map<String,String> resp = new HashMap<>();
                resp.put("message","Email successfully sent");
                return new ResponseEntity<>(resp,HttpStatus.OK);
            }
            else throw new ApiRequestException("Sending email failed", BAD_REQUEST);


        }catch (Exception e){
            if(e.getMessage().equals("User not found")) throw new ApiRequestException("User not found", NOT_FOUND);
            throw new ApiRequestException("Sending email failed", BAD_REQUEST);
        }


    }

    public String createToken(User user, int expirationTimeMin){
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_USER");
        Algorithm algorithm = Algorithm.HMAC256("promijeniSecretToken12312213sadadse12312312312312312b12312");

        String access_token = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (long) expirationTimeMin *60*1000))
                .withIssuer(user.getEmail())
                .withClaim("roles", authorities)
                .sign(algorithm);

        return access_token;
    }

    public ResponseEntity<Object> changePassword(String token, String password) {
        try{
            Algorithm algorithm = Algorithm.HMAC256("promijeniSecretToken12312213sadadse12312312312312312b12312");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String email = decodedJWT.getSubject();

            Optional<User> foundUser = userRepository.findUserByEmail(email);
            if(!foundUser.isPresent()) throw new ApiRequestException("User not found", NOT_FOUND);

            User foundU = foundUser.get();
            foundU.getPassword().setPassword(passwordEncoder.encode(password));
            userRepository.save(foundU);

            Map<String,String> resp = new HashMap<>();
            resp.put("message","Password successfully changed");
            return new ResponseEntity<>(resp,HttpStatus.OK);


        }catch (Exception e){
            throw new ApiRequestException("Password change time has expired", BAD_REQUEST);
        }

    }
}
