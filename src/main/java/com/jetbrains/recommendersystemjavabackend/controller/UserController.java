package com.jetbrains.recommendersystemjavabackend.controller;

import com.jetbrains.recommendersystemjavabackend.api.UsersApi;
import com.jetbrains.recommendersystemjavabackend.model.User;
import com.jetbrains.recommendersystemjavabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController implements UsersApi {

    private UserService userService;

    @Override
    public ResponseEntity<List<User>> usersGet() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Override
    public ResponseEntity<User> usersPost(User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }


}
