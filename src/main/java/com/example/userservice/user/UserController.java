package com.example.userservice.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RefreshScope
@RestController
@RequestMapping(path = "user")
public class UserController {
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/all")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @RequestMapping(params = "email", method = RequestMethod.GET)
    @GetMapping()
    public User getUserByEmail(@RequestParam(value = "email") String email){
        return userService.getUserByEmail(email);
    }

    @RequestMapping(params = "id", method = RequestMethod.GET)
    @GetMapping()
    public User getUserById(@RequestParam(value = "id") Long id){
        return userService.getUserById(id);
    }

    @RequestMapping(params = "passwordId", method = RequestMethod.GET)
    @GetMapping
    public ResponseEntity<Object> getUserEmailByPasswordId(@RequestParam(value = "passwordId") Long passwordId){
        return userService.getUserEmailByPasswordId(passwordId);
    }

    @PostMapping()
    public User createNewUser(@RequestBody User user){
        return userService.createNewUser(user, user.getPassword().getPassword());
    }

    @PutMapping()
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @PutMapping("/update")
    public User updateUserCheckPw(@RequestBody User user, @RequestParam(value = "pw") String pw){
        return userService.updateUserCheckPw(user, pw);
    }

    @RequestMapping(params = "email", method = RequestMethod.DELETE)
    @DeleteMapping()
    public void deleteUserById(@RequestParam(value = "email") String email){
        userService.deleteUserByEmail(email);
    }

    @RequestMapping(params = "id", method = RequestMethod.DELETE)
    @DeleteMapping()
    public void deleteUserById(@RequestParam(value = "id") Long id){
        userService.deleteUserById(id);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<Object> refreshToken(@RequestHeader HttpHeaders headers) {
        //String token = headers.getFirst(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        return userService.refreshToken(headers);
    }

    @PostMapping(path = "/forgotPassword")
    public ResponseEntity<Object> sendForgotPasswordEmail(@RequestParam(value = "email") String email){
        return userService.sendForgotPasswordEmail(email);
    }

    @PutMapping(path = "/changePassword/{token}")
    public ResponseEntity<Object> changePassword(@PathVariable String token, @RequestBody() Map<String, String> password){
        String pw = password.get("password");
        return userService.changePassword(token, pw);
    }

}
