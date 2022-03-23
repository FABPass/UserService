package com.example.userservice.user;

import com.example.userservice.mfa.Mfa;
import com.example.userservice.password.Password;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository userRepository){
        return args -> {
            User belmin = new User(
                    "Belmin",
                    "Selimovic",
                    "bselimovic1@etf.unsa.ba",
                    "555-555/221",
                    new Password(
                            "sifra123",
                            LocalDateTime.now()
                    ),
                    new Mfa(
                            "Application",
                            "11231b23hj12h3kh12k3hjk12h3jk12hjk3h12jk3hjk12h3kj12j3k12",
                            false
                    )
            );
            User faris = new User(
                    "Faris",
                    "Music",
                    "fmusic2@etf.unsa.ba",
                    "444-444/221",
                    new Password(
                            "sifra321",
                            LocalDateTime.now()
                    ),
                    new Mfa(
                            "Phone",
                            "178456",
                            true
                    )
            );
            User ahmed = new User(
                    "Ahmed",
                    "Pasic",
                    "apasic2@etf.unsa.ba",
                    "333-333/221",
                    new Password(
                            "sifra111",
                            LocalDateTime.now()
                    ),
                    new Mfa(
                            "None",
                            "",
                            false
                    )
            );

            userRepository.saveAll(List.of(belmin, faris, ahmed));
        };
    }
}
