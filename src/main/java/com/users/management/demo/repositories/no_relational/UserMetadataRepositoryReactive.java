package com.users.management.demo.repositories.no_relational;

import com.users.management.demo.repositories.no_relational.model.UserMetadataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserMetadataRepositoryReactive extends ReactiveMongoRepository<UserMetadataEntity, String> {
    Mono<UserMetadataEntity> findByUserId(String userId);
}