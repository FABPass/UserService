package com.example.userservice.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository){
        return args -> {
            User belmin = new User(
                    "Belmin",
                    "Selimovic",
                    "bselimovic1@etf.unsa.ba",
                    "555-555/221",
                    1L
            );
            User faris = new User(
                    "Faris",
                    "Music",
                    "fmusic2@etf.unsa.ba",
                    "444-444/221",
                    2L
            );
            User ahmed = new User(
                    "Ahmed",
                    "Pasic",
                    "apasic2@etf.unsa.ba",
                    "333-333/221",
                    3L
            );

            repository.saveAll(List.of(belmin, faris, ahmed));
        };
    }
}
