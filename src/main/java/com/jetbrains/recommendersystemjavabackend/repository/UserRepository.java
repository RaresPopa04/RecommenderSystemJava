package com.jetbrains.recommendersystemjavabackend.repository;

import com.jetbrains.recommendersystemjavabackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByFileId(Long fileId);
}
