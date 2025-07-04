package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.User;
import mk.ukim.finki.wp.repoagregator.repository.UserRepository;
import mk.ukim.finki.wp.repoagregator.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("findById со валиден username - треба да го врати корисникот")
    void testFindByIdSuccess() {
        String username = "testuser";
        User mockUser = new User();
        mockUser.setName(username);
        mockUser.setEmail("test@example.com");
        when(userRepository.findByid(username)).thenReturn(Optional.of(mockUser));
        User result = userService.findById(username);

        assertNotNull(result);
        assertEquals(username, result.getName());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByid(username);
    }

    @Test
    @DisplayName("findById со невалиден username - треба да фрли RuntimeException")
    void testFindByIdUserNotFound() {
        String username = "nevalidenuser";
        when(userRepository.findByid(username)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(username);
        });

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepository).findByid(username);
    }

    @Test
    @DisplayName("findById со null username - треба да фрли RuntimeException")
    void testFindByIdNullUsername() {
        String username = null;
        when(userRepository.findByid(username)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(username);
        });

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepository).findByid(username);
    }

    @Test
    @DisplayName("findById со празен string username - треба да фрли RuntimeException")
    void testFindByIdEmptyUsername() {
        String username = "";
        when(userRepository.findByid(username)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(username);
        });

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepository).findByid(username);
    }

    @Test
    @DisplayName("findById со whitespace username - треба да фрли RuntimeException")
    void testFindByIdWhitespaceUsername() {
        String username = "   ";
        when(userRepository.findByid(username)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(username);
        });

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepository).findByid(username);
    }

    @Test
    @DisplayName("Тестирање на repository интеракција - дали се повикува точно пати")
    void testRepositoryInteraction() {
        String username = "testuser";
        User mockUser = new User();
        mockUser.setName(username);
        when(userRepository.findByid(username)).thenReturn(Optional.of(mockUser));

        userService.findById(username);
        userService.findById(username);

        verify(userRepository, times(2)).findByid(username);
    }
}