package com.users.management.demo.repositories.no_relational.model;

import com.users.management.demo.controller.dto.UserMetadataDto;
import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_metadata")
public class UserMetadataEntity {

    public UserMetadataEntity(Long userId, Map<String, String> userMetadata) {
        this.userId = userId;
        this.preferences = userMetadata;
    }

    @Id
    private ObjectId id;
    private Long userId;
    private Map<String, String> preferences;

}
