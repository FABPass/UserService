package com.example.userservice.password;

import com.example.userservice.user.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class PasswordConfig {

    @Bean
    CommandLineRunner passwordCommandLineRunner(PasswordRepository passwordRepository){
        return args -> {
//            Password pw1 = new Password(
//                    "sifra123",
//                    LocalDateTime.now()
//            );
//
//            Password pw2 = new Password(
//                    "sifra321",
//                    LocalDateTime.now()
//            );
//
//            Password pw3 = new Password(
//                    "sifra111",
//                    LocalDateTime.now()
//            );
//
//            passwordRepository.saveAll(List.of(pw1,pw2,pw3));
        };
    }
}
