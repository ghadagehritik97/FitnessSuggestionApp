package com.fitness.userService.controller;


import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Validated
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    @GetMapping("/getUser/{userEmail}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userEmail) {
        logger.info("Fetching user with id: {}", userEmail);
        UserResponse userResponse=userService.getUser(userEmail);
        return new ResponseEntity<>(userResponse,HttpStatus.OK);

    }
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        logger.info("Fetching user with id: {}", userId);
        Boolean isValidUser=userService.validateUser(userId);
        return new ResponseEntity<>(isValidUser,HttpStatus.OK);

    }

    @PostMapping("/registerUser")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        logger.info("Registering user with email: {}", userRequest.getEmail());
        UserResponse userResponse=userService.registerUser(userRequest);
        return new ResponseEntity<>(userResponse,HttpStatus.CREATED);
    }

}
