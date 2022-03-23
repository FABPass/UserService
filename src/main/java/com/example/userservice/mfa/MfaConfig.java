package com.example.userservice.mfa;

import com.example.userservice.user.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MfaConfig {

    @Bean
    CommandLineRunner mfaCommandLineRunner(MfaRepository mfaRepository){
        return args -> {
//            Mfa mfa1 = new Mfa(
//                    "Application",
//                    "11231b23hj12h3kh12k3hjk12h3jk12hjk3h12jk3hjk12h3kj12j3k12",
//                    false
//
//            );
//
//            Mfa mfa2 = new Mfa(
//                    "Phone",
//                    "178456",
//                    true
//            );
//
//            Mfa mfa3 = new Mfa(
//                    "None",
//                    "",
//                    false
//
//            );
//
//            mfaRepository.saveAll(List.of(mfa1, mfa2, mfa3));

        };

    }
}
