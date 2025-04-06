package com.telerikacademy.web.cryptocurrency_trading_platform.services;

import com.telerikacademy.web.cryptocurrency_trading_platform.exceptions.*;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import com.telerikacademy.web.cryptocurrency_trading_platform.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private User user1, user2, user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("john_doe");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPhoneNumber("1234567890");
        user1.setAdmin(true);
        user1.setBlocked(false);
        user1.setBalance(1000.0);

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("jane_smith");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPhoneNumber("0987654321");
        user2.setAdmin(false);
        user2.setBlocked(true);
        user2.setBalance(1000.0);

        user3 = new User();
        user3.setId(3L);
        user3.setUsername("jane_smith");
        user3.setFirstName("Jane");
        user3.setLastName("Smith");
        user3.setEmail("jane.smith@example.com");
        user3.setPhoneNumber("0987654321");
        user3.setAdmin(false);
        user3.setBlocked(true);
        user3.setBalance(1000.0);
    }

    @Test
    void getById_Should_Return_User() {
        // Arrange
        when(userRepository.findUserById(anyLong())).thenReturn(user1);

        // Act
        User result = userService.getById(1L);

        // Assert
        Assertions.assertEquals(user1.getUsername(), result.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findUserById(anyLong());
    }

    @Test
    void getByEmail_Should_Return_User() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(user1);

        // Act
        User result = userService.getByEmail("john.doe@example.com");

        // Assert
        Assertions.assertEquals(user1.getUsername(), result.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(anyString());
    }

    @Test
    void getByUsername_Should_Return_User() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(user1);

        // Act
        User result = userService.getByUsername("john_doe");

        // Assert
        Assertions.assertEquals(user1.getUsername(), result.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(anyString());
    }

    @Test
    void getByPhoneNumber_Should_Return_User() {
        // Arrange
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(user1);

        // Act
        User result = userService.getByPhoneNumber("1234567890");

        // Assert
        Assertions.assertEquals(user1.getUsername(), result.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void Create_Should_Throw_UserWithUsernameAlreadyExists() {

        when(userRepository.findByEmail(anyString())).thenReturn(user1);

        // Arrange, Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.create(user1));
    }

    @Test
    void Create_Should_Create_WhenValid() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("testUser@gmail.com");

        Mockito.when(userRepository.findByEmail("testUser@gmail.com"))
                .thenReturn(null);

        // Act
        userService.create(newUser);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).save(newUser);
    }

    @Test
    void Update_Should_Throw_When_UserNotAdminOrCreator() {
        // Arrange, Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.update(user2, user3, user2.getId()));
    }

//    @Test
//    void Update_Should_Throw_When_UserWithEmailExists() {
//
//        user1.setAdmin(true);
//        // Arrange
//        user2.setEmail("jane.smith@example.com");
//
//        // Act & Assert
//        Assertions.assertThrows(DuplicateEntityException.class,
//                () -> userService.update(user2, user1, user1.getId()));
//    }

    @Test
    void Update_Should_Update_WhenValid() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setUsername("john_doe");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("test@example.com");
        updatedUser.setUsername("updated_john_doe");

        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(existingUser);
        Mockito.when(userService.getByEmail("test@example.com")).thenReturn(existingUser);

        userService.update(updatedUser, existingUser, existingUser.getId());

        Mockito.verify(userRepository, Mockito.times(1)).save(updatedUser);
    }

    @Test
    void delete_Should_Throw_When_UserNotFound() {
        // Arrange
        Mockito.when(userRepository.findUserById(anyLong())).thenReturn(user1);

        Mockito.when(userService.getById(anyLong())).thenThrow(new EntityNotFoundException("User", "username", user1.getUsername()));

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.delete(anyLong(), user1));
    }

    @Test
    void delete_Should_Delete_WhenValid() {
        // Arrange
        User adminUser = user1;
        User user = user2;
        when(userRepository.findUserById(anyLong())).thenReturn(user);

        Mockito.when(userService.getById(anyLong()))
                .thenReturn(user);

        // Act
        userService.delete(adminUser.getId(), user);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }
}
