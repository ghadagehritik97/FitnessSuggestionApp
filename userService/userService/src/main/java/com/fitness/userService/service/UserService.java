package com.fitness.userService.service;

import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse getUser(String userId);

    UserResponse registerUser(@Valid UserRequest userRequest);
}
