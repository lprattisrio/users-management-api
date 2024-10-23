package com.users.management.demo.repositories.no_relational;

import com.users.management.demo.repositories.no_relational.model.UserMetadataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMetadataRepository extends MongoRepository<UserMetadataEntity, String> {}