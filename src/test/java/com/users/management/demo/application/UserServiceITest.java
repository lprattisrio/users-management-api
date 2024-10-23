package com.users.management.demo.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@RequiredArgsConstructor
class UserServiceITest {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserMetadataRepository userMetadataRepository;

    @Test
    void testCompensationWhenMetadataSaveFails() {
        long totalUsersStart = userRepository.count();
        long totalRolesStart = roleRepository.count();

        when(userMetadataRepository.save(any(UserMetadataEntity.class)))
                .thenThrow(new RuntimeException("MongoDB save failed"));
        UserDTO newUser = new UserDTO(
                new UserDataDto(null, "testUser", "user@test.com", "test_role"),
                new UserMetadataDto(Map.of("headerColor", "blue")));

        assertThrows(RuntimeException.class, () -> userService.register(newUser));

        verify(userMetadataRepository, times(1)).save(any(UserMetadataEntity.class));
        assertEquals(totalRolesStart, roleRepository.count());
        assertEquals(totalUsersStart, userRepository.count());
    }

}
