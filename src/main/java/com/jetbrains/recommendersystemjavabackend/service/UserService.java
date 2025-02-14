package com.jetbrains.recommendersystemjavabackend.service;

import com.jetbrains.recommendersystemjavabackend.entity.GenreEntity;
import com.jetbrains.recommendersystemjavabackend.entity.UserEntity;
import com.jetbrains.recommendersystemjavabackend.kafka.AvroProducerService;
import com.jetbrains.recommendersystemjavabackend.model.User;
import com.jetbrains.recommendersystemjavabackend.model.UserPage;
import com.jetbrains.recommendersystemjavabackend.model.UserPut;
import com.jetbrains.recommendersystemjavabackend.repository.GenreRepository;
import com.jetbrains.recommendersystemjavabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public final AvroProducerService avroProducerService;
    private final GenreRepository genreRepository;

    public UserPage getUsers(int pageNo, int pageSize) {
        Page<UserEntity> page = userRepository.findAll(PageRequest.of(pageNo, pageSize));
        UserPage userPage = new UserPage();
        userPage.setContent(page.stream().map(UserEntity::fromEntity).toList());
        userPage.setTotalPages(page.getTotalPages());
        userPage.setNumber(page.getNumber());
        userPage.setSize(page.getSize());
        userPage.setTotalElements((int) page.getTotalElements());
        return userPage;
    }

    public User createUser(UserPut user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());

        user.getGenres().forEach(genre -> {
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if (foundGenre.isEmpty()) {
                throw new IllegalArgumentException("Genre not found");
            }
        });
        userEntity.setPreferredGenres(genreRepository.findAllById(user.getGenres()));
        userEntity.setFileId(userRepository.findMaxFileId() + 1);
        //TODO repair password
        userEntity.setPassword(UUID.randomUUID().toString());
        User saved = userRepository.save(userEntity).fromEntity();
        avroProducerService.sendUserEvent(userEntity.getFileId(), user.getGenres());
        return saved;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).map(UserEntity::fromEntity).orElse(null);
    }

    public User updateUser(Long id, UserPut user) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isEmpty()) {
            return null;
        }
        UserEntity currentUserEntity = userEntity.get();
        user.getGenres().forEach(genre -> {
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if (foundGenre.isEmpty()) {
                throw new IllegalArgumentException("Genre not found");
            }
        });
        if (userRepository.existsByUsername(user.getUsername()) && !currentUserEntity.getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        currentUserEntity.setPreferredGenres(genreRepository.findAllById(user.getGenres()));

        User saved = userRepository.save(currentUserEntity).fromEntity();
        avroProducerService.sendUserEvent(currentUserEntity.getFileId(), user.getGenres());
        return saved;
    }

}
