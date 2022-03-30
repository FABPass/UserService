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
    public User getUserByEmail(@RequestParam(value = "id") Long id){
        return userService.getUserById(id);
    }

    @PostMapping()
    public User createNewUser(@RequestBody User user){
        return userService.createNewUser(user, user.getPassword().getPassword());
    }

    @PutMapping()
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
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

}
