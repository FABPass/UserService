package com.example.userservice.rabbitmq;

import com.example.userservice.exception.ApiRequestException;
import com.example.userservice.user.User;
import com.example.userservice.user.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Listener {

    public static final String NOTIFICATION_SERVICE_QUEUE = "notification-service-queue";
    private final UserService userService;

    @Autowired
    public Listener(UserService userService) {
        this.userService = userService;
    }


    @RabbitListener(queues = NOTIFICATION_SERVICE_QUEUE)
    public void deleteUserFromQueue(Event event){
        if(event.getMessage().equals("Delete user")){
            try {
                User user = userService.getUserByEmail(event.getUserEmail());
                if(user!=null)
                    userService.deleteUserByEmail(event.getUserEmail());
            }catch (Exception e){
                throw new ApiRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
