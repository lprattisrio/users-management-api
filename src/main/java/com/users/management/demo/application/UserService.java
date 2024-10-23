package com.users.management.demo.application;

import com.users.management.demo.controller.dto.UserDTO;
import com.users.management.demo.controller.dto.UserDataDto;
import com.users.management.demo.controller.dto.UserMetadataDto;
import com.users.management.demo.repositories.no_relational.UserMetadataRepository;
import com.users.management.demo.repositories.no_relational.model.UserMetadataEntity;
import com.users.management.demo.repositories.relational.RoleRepository;
import com.users.management.demo.repositories.relational.UserRepository;
import com.users.management.demo.repositories.relational.model.RoleEntity;
import com.users.management.demo.repositories.relational.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMetadataRepository userMetadataRepository;

    private final EmailService emailService;

    @Transactional
    public UserDTO register(UserDTO userDto) {

        final UserDataDto userData = userDto.data();

        // 1 - Get or save new role
        RoleEntity role = getRoleEntity(userData);

        // 2 - Save user
        UserEntity user = userRepository.save(new UserEntity(userData.name(), userData.email(), role));

        // 3 - Save user metadata
        final UserMetadataDto userMetadataDto = saveMetadataUser(userDto, user);

        // 4 - Send email
        emailService.sendRegistrationEmail(user);

        // Map response
        final UserDataDto dataDto = new UserDataDto(user.getId(), user.getName(), user.getEmail(), user.getRole().getName());
        return new UserDTO(dataDto, userMetadataDto);
    }

    private UserMetadataDto saveMetadataUser(UserDTO userDto, UserEntity user) {
        UserMetadataDto userMetadataDto = null;
        if (userDto.metadata() != null) {
            UserMetadataEntity metadata = userMetadataRepository.save(
                    new UserMetadataEntity(user.getId(), userDto.metadata().preferences()));
            userMetadataDto = new UserMetadataDto(metadata.getPreferences());
        }
        return userMetadataDto;
    }

    private RoleEntity getRoleEntity(UserDataDto userData) {
        return roleRepository.findByName(userData.role())
                .orElseGet(() -> roleRepository.save(new RoleEntity(userData.role())));
    }
}
