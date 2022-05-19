package com.example.userservice.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.userservice.exception.ApiRequestException;
import com.example.userservice.mfa.Mfa;
import com.example.userservice.password.Password;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            throw new ApiRequestException("There is no user registered with email: "+ email, HttpStatus.BAD_REQUEST);
        }
        return userByEmail.get();
    }

    public User getUserById(Long id){
        Optional<User> userById = userRepository.findUserById(id);
        if(!userById.isPresent()){
            throw new ApiRequestException("There is no user with id: "+ id, HttpStatus.BAD_REQUEST);
        }
        return userById.get();
    }


    public ResponseEntity<Object> getUserEmailByPasswordId(Long passwordId) {
        Optional<User> userByPasswordId = userRepository.findUserByPasswordId(passwordId);
        if(!userByPasswordId.isPresent()){
            throw new ApiRequestException("There is no user with passwordId: "+ passwordId, HttpStatus.BAD_REQUEST);
        }
        JSONObject email = new JSONObject();
        email.put("email", userByPasswordId.get().getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(email.toString());
    }

    public User createNewUser(User user, String password) {
        addOrUpdateUserValidation(user);
        if(password.isEmpty()) throw new ApiRequestException("Password is empty!", HttpStatus.BAD_REQUEST);

        Password pw = new Password(passwordEncoder.encode(password), LocalDateTime.now());
        Mfa mfa = new Mfa("None","",false);
        user.setPassword(pw);
        user.setMfa(mfa);
        try{
            return userRepository.save(user);
        }
        catch (Exception e){
            throw new ApiRequestException("You can not add user with existing email or phone", HttpStatus.BAD_REQUEST);
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
        else throw new ApiRequestException("There is no user with id: "+ user.getId(), HttpStatus.BAD_REQUEST);

    }


    public void deleteUserById(Long id) {
        Optional<User> userById = userRepository.findUserById(id);
        if(userById.isPresent()) userRepository.deleteById(id);
        else throw new ApiRequestException("There is no user with id: "+ id, HttpStatus.BAD_REQUEST);
    }

    public void deleteUserByEmail(String email) {
        Optional<User> userById = userRepository.findUserByEmail(email);
        if(userById.isPresent()) userRepository.deleteById(userById.get().getId());
        else throw new ApiRequestException("There is no user registered with email: "+ email, HttpStatus.BAD_REQUEST);
    }

    public void addOrUpdateUserValidation(User user){
        if(user.getName().isEmpty()) throw new ApiRequestException("Name is empty!", HttpStatus.BAD_REQUEST);
        if(user.getSurname().isEmpty()) throw new ApiRequestException("Surname is empty!", HttpStatus.BAD_REQUEST);
        if(user.getEmail().isEmpty()) throw new ApiRequestException("Email is empty!", HttpStatus.BAD_REQUEST);
        if(user.getPhone().isEmpty()) throw new ApiRequestException("Phone is empty!", HttpStatus.BAD_REQUEST);
        String regexPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(!Pattern.compile(regexPattern).matcher(user.getEmail()).matches()) throw new ApiRequestException("Email format is not valid. Please provide valid email", HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<>(tokens, HttpStatus.OK);

            }catch(Exception exception){
                HashMap<String, String> hash = new HashMap<>();
                hash.put("error",exception.getMessage());
                return new ResponseEntity<>(hash, FORBIDDEN);
            }
        }else{
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
