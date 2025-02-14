package com.jetbrains.recommendersystemjavabackend.service;

import com.jetbrains.recommendersystemjavabackend.entity.UserEntity;
import com.jetbrains.recommendersystemjavabackend.kafka.AvroProducerService;
import com.jetbrains.recommendersystemjavabackend.model.User;
import com.jetbrains.recommendersystemjavabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public final AvroProducerService avroProducerService;

    public List<User> getUsers() {
        return userRepository.findAll().stream().map(UserEntity::fromEntity).toList();
    }

    public User createUser(User user) {
        UserEntity userEntity = UserEntity.toEntity(user);
        userEntity.setFileId(userRepository.findMaxFileId() + 1);
        User saved =  userRepository.save(userEntity).fromEntity();
        avroProducerService.sendUserEvent(userEntity.getFileId(), List.of());
        return saved;
    }

}
