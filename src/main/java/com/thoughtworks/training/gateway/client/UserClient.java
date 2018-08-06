package com.thoughtworks.training.gateway.client;


import com.thoughtworks.training.gateway.dto.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {
    @PostMapping("/verifications")
    User verifyToken(String token);
}
