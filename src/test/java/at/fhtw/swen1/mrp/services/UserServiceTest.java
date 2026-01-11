package at.fhtw.swen1.mrp.services;

import at.fhtw.swen1.mrp.business.entities.User;
import at.fhtw.swen1.mrp.data.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordHasher);
    }

    @Test
    void registerUser_withValidCredentials_shouldCreateUser() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordHasher.hash("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_withExistingUsername_shouldThrowException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("existinguser", "password123")
        );
    }

    @Test
    void registerUser_withEmptyUsername_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("", "password123")
        );
    }

    @Test
    void registerUser_withEmptyPassword_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("testuser", "")
        );
    }

    @Test
    void loginUser_withValidCredentials_shouldReturnUser() {
        User user = new User("testuser", "hashedPassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHasher.verify("password123", "hashedPassword")).thenReturn(true);

        Optional<User> result = userService.loginUser("testuser", "password123");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void loginUser_withInvalidPassword_shouldReturnEmpty() {
        User user = new User("testuser", "hashedPassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHasher.verify("wrongpassword", "hashedPassword")).thenReturn(false);

        Optional<User> result = userService.loginUser("testuser", "wrongpassword");

        assertTrue(result.isEmpty());
    }

    @Test
    void loginUser_withNonExistentUser_shouldReturnEmpty() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.loginUser("nonexistent", "password123");

        assertTrue(result.isEmpty());
    }

    @Test
    void updateProfile_withValidData_shouldUpdateUser() {
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "hashedPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateProfile(userId, "test@email.com", "sci-fi");

        assertEquals("test@email.com", result.getEmail());
        assertEquals("sci-fi", result.getFavoriteGenre());
    }
}