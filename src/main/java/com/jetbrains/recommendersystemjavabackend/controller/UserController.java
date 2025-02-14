package com.jetbrains.recommendersystemjavabackend.controller;

import com.jetbrains.recommendersystemjavabackend.api.UsersApi;
import com.jetbrains.recommendersystemjavabackend.model.User;
import com.jetbrains.recommendersystemjavabackend.model.UserPage;
import com.jetbrains.recommendersystemjavabackend.model.UserPut;
import com.jetbrains.recommendersystemjavabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserPage> usersGet(Integer pageNo, Integer pageSize) {
        return ResponseEntity.ok(userService.getUsers(pageNo, pageSize));
    }

    @Override
    public ResponseEntity<User> usersPost(UserPut user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Override
    public ResponseEntity<User> usersIdGet(Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<User> usersIdPut(Long id, UserPut userPut) {
        return ResponseEntity.ok(userService.updateUser(id, userPut));
    }
}
