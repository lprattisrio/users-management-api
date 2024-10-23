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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMetadataRepository userMetadataRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserWithNewRoleAndMetadata() {
        // Arrange
        UserDataDto userData = new UserDataDto(null, "Test Doe", "test.doe@test.com", "USER_ROLE");
        UserMetadataDto metadataDto = new UserMetadataDto(Map.of("testKey", "testValue") );
        UserDTO userDto = new UserDTO(userData, metadataDto);

        RoleEntity roleEntity = new RoleEntity("USER_ROLE");
        roleEntity.setId(1L);
        when(roleRepository.findByName("USER_ROLE")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(roleEntity);

        UserEntity userEntity = new UserEntity("Test Doe", "test.doe@test.com", roleEntity);
        userEntity.setId(1L);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserMetadataEntity metadataEntity = new UserMetadataEntity(1L, Map.of("testKey", "testValue"));
        when(userMetadataRepository.save(any(UserMetadataEntity.class))).thenReturn(metadataEntity);

        // Act
        UserDTO result = userService.register(userDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Doe", result.data().name());
        assertEquals("USER_ROLE", result.data().role());
        assertEquals(Map.of("testKey", "testValue"), result.metadata().preferences());

        verify(roleRepository, times(1)).findByName("USER_ROLE");
        verify(roleRepository, times(1)).save(any(RoleEntity.class));
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMetadataRepository, times(1)).save(any(UserMetadataEntity.class));
    }

    @Test
    void testRegisterUserWithExistingRoleAndNoMetadata() {
        // Arrange
        UserDataDto userData = new UserDataDto(null, "Jane Doe", "jane.doe@example.com", "ADMIN_ROLE");
        UserDTO userDto = new UserDTO(userData, null);

        RoleEntity roleEntity = new RoleEntity("ADMIN_ROLE");
        roleEntity.setId(2L);
        when(roleRepository.findByName("ADMIN_ROLE")).thenReturn(Optional.of(roleEntity));

        UserEntity userEntity = new UserEntity("Jane Doe", "jane.doe@example.com", roleEntity);
        userEntity.setId(2L);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        UserDTO result = userService.register(userDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.data().name());
        assertEquals("ADMIN_ROLE", result.data().role());
        assertNull(result.metadata());

        verify(roleRepository, times(1)).findByName("ADMIN_ROLE");
        verify(roleRepository, never()).save(any(RoleEntity.class));
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMetadataRepository, never()).save(any(UserMetadataEntity.class));
    }

    @Test
    void testRegisterUserWithExceptionInUserSave() {
        // Arrange
        UserDataDto userData = new UserDataDto(null, "Test Doe", "test.doe@test.com", "USER_ROLE");
        UserDTO userDto = new UserDTO(userData, null);

        RoleEntity roleEntity = new RoleEntity("USER_ROLE");
        when(roleRepository.findByName("USER_ROLE")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(roleEntity);
        when(userRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Error saving user"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(userDto));

        assertEquals("Error saving user", exception.getMessage());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUserWithExceptionInMetadataSave() {
        // Arrange
        UserDataDto userData = new UserDataDto(null, "Test Doe", "test.doe@test.com", "USER_ROLE");
        UserMetadataDto metadataDto = new UserMetadataDto(Map.of("testKey", "testValue"));
        UserDTO userDto = new UserDTO(userData, metadataDto);

        RoleEntity roleEntity = new RoleEntity("USER_ROLE");
        when(roleRepository.findByName("USER_ROLE")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(roleEntity);

        UserEntity userEntity = new UserEntity("Test Doe", "test.doe@test.com", roleEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMetadataRepository.save(any(UserMetadataEntity.class))).thenThrow(new RuntimeException("Error saving metadata"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(userDto));

        assertEquals("Error saving metadata", exception.getMessage());
    }

    @Test
    void testRegisterUserWithExceptionInUserSaveAndNewRole() {
        // Arrange
        UserDataDto userData = new UserDataDto(null, "Test Doe", "test.doe@test.com", "NEW_ROLE");
        UserDTO userDto = new UserDTO(userData, null);

        RoleEntity roleEntity = new RoleEntity("NEW_ROLE");
        when(roleRepository.findByName("NEW_ROLE")).thenReturn(Optional.of(roleEntity));
        when(userRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Error saving user"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(userDto));

        assertEquals("Error saving user", exception.getMessage());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testRegisterUserWithExceptionInMetadataSaveAndNewRole() {
        // Arrange
        UserDataDto userData = new UserDataDto(null, "Test Doe", "test.doe@test.com", "NEW_ROLE");
        UserMetadataDto metadataDto = new UserMetadataDto(Map.of("testKey", "testValue"));
        UserDTO userDto = new UserDTO(userData, metadataDto);

        RoleEntity roleEntity = new RoleEntity("NEW_ROLE");
        when(roleRepository.findByName("NEW_ROLE")).thenReturn(Optional.of(roleEntity));

        UserEntity userEntity = new UserEntity("Test Doe", "test.doe@test.com", roleEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMetadataRepository.save(any(UserMetadataEntity.class))).thenThrow(new RuntimeException("Error saving metadata"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(userDto));
        assertEquals("Error saving metadata", exception.getMessage());

    }

}
