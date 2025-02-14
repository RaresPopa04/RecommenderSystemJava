package com.jetbrains.recommendersystemjavabackend.repository;

import com.jetbrains.recommendersystemjavabackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByFileId(Long fileId);

    @Query("SELECT max(user.fileId) from UserEntity user")
    Long findMaxFileId();

    boolean existsByUsername(@NotNull String username);
}
