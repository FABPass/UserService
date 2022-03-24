package com.example.userservice.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "user")
public class UserController {
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping(path = "/email")
    public User getUserByEmail(@RequestParam(value = "email") String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping(path = "/id")
    public User getUserByEmail(@RequestParam(value = "id") Long id){
        return userService.getUserById(id);
    }

    @PostMapping(path = "/create")
    public User createNewUser(@RequestBody User user){
        return userService.createNewUser(user, user.getPassword().getPassword());
    }

    @PutMapping(path = "/update")
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @DeleteMapping(path = "/delete/email")
    public void deleteUserById(@RequestParam(value = "email") String email){
        userService.deleteUserByEmail(email);
    }

    @DeleteMapping(path = "/delete/id")
    public void deleteUserById(@RequestParam(value = "id") Long id){
        userService.deleteUserById(id);
    }

}
