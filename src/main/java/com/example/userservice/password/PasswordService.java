package com.example.userservice.password;

import com.example.userservice.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;

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
        Optional<Password> pass = passwordRepository.findPasswordById(password.getId())
                .map(pw ->{
                    pw.setPassword(password.getPassword());
                    pw.setCreatedAt(LocalDateTime.now());
                    return passwordRepository.save(pw);
                });
        if(pass.isPresent()) return pass.get();
        else throw new ApiRequestException("There is no password with id: "+ password.getId(), HttpStatus.BAD_REQUEST);
    }
}
