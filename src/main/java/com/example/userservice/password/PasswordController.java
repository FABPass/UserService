package com.example.userservice.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping(path = "password")
public class PasswordController {

    private final PasswordService passwordService;

    @Autowired
    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping(path = "/all")
    public List<Password> getPassword(){
        return passwordService.getPasswords();
    }

    @GetMapping()
    public Password getPasswordById(@RequestParam(value="id") Long id){
        return passwordService.getPasswordById(id);
    }

    @PutMapping()
    public Password updatePassword(@RequestBody Password password){
        return passwordService.updatePassword(password);
    }

}
