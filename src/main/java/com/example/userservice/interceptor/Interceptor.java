package com.example.userservice.interceptor;

import com.example.userservice.grpc.UserGrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class Interceptor implements HandlerInterceptor {

    private final UserGrpcClient userGrpcClient;

    @Autowired
    public Interceptor(UserGrpcClient mainGrpcClient) {
        this.userGrpcClient = mainGrpcClient;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Logger logger = LoggerFactory.getLogger(Interceptor.class);
        String grpcResponse = userGrpcClient.log("UserService", "Belmin", request.getMethod(), HttpStatus.valueOf(response.getStatus()).toString());
        logger.info(grpcResponse);
    }

}
