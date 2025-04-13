package com.festiva.datastorage.mongo;

import com.festiva.datastorage.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByTelegramUserId(long telegramUserId);
}

