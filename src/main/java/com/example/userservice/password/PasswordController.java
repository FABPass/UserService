package com.example.userservice.password;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @GetMapping(path = "/checkPasswordExpiration")
    public ResponseEntity<Object> checkPasswordExpirationAndNotifyUser(){
        return passwordService.checkPasswordExpirationAndNotifyUser();
    }

}
