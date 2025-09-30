package com.fitness.userService.service;

import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.entity.User;
import com.fitness.userService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    @Override
    public UserResponse getUser(String userId) {
        User user=userRepository.findByEmail(userId).orElseThrow(()->new RuntimeException("User with id "+userId+" not found"));
        UserResponse response=new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());
        response.setPassword(user.getPassword());
        return response;
    }

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        userRepository.findByEmail(userRequest.getEmail()).ifPresent(u->{
            throw new RuntimeException("User with email "+userRequest.getEmail()+" already exists");
        });
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        User savedUser=userRepository.save(user);
        UserResponse response=new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedDate(savedUser.getCreatedDate());
        response.setUpdatedDate(savedUser.getUpdatedDate());
        response.setPassword(savedUser.getPassword());
        return response;
    }

    @Override
    public Boolean validateUser(String userId) {
        return userRepository.existsById(userId);
    }
}
