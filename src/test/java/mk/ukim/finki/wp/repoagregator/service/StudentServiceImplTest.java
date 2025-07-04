package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.Student;
import mk.ukim.finki.wp.repoagregator.repository.StudentRepository;
import mk.ukim.finki.wp.repoagregator.service.impl.StudentServiceImpl;
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
class StudentServiceImplTest {

    private StudentService studentService;
    private StudentRepository studentRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        StudentRepository studentRepository() {
            return mock(StudentRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentServiceImpl(studentRepository);
    }

    @Test
    @DisplayName("findByEmail со валиден email - треба да го врати студентот")
    void testFindByEmailSuccess() {
        String email = "test.student@students.finki.ukim.mk";
        Student mockStudent = new Student();
        mockStudent.setEmail(email);
        mockStudent.setName("Тест Студент");
        mockStudent.setIndex("000000");

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(mockStudent));

        Student result = studentService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("Тест Студент", result.getName());
        assertEquals("000000", result.getIndex());
        verify(studentRepository).findByEmail(email);
    }

    @Test
    @DisplayName("findByEmail со невалиден email - треба да фрли RuntimeException")
    void testFindByEmailNotFound() {
        String email = "nepostoecki.student@students.finki.ukim.mk";

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.findByEmail(email);
        });

        assertNotNull(exception);
        verify(studentRepository).findByEmail(email);
    }

    @Test
    @DisplayName("findByEmail со null email - треба да фрли RuntimeException")
    void testFindByEmailNullEmail() {
        String email = null;

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.findByEmail(email);
        });

        assertNotNull(exception);
        verify(studentRepository).findByEmail(email);
    }

    @Test
    @DisplayName("findByEmail со празен email - треба да фрли RuntimeException")
    void testFindByEmailEmptyEmail() {
        String email = "";

        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.findByEmail(email);
        });

        assertNotNull(exception);
        verify(studentRepository).findByEmail(email);
    }

    @Test
    @DisplayName("findByEmail со валиден email со различен домен - треба да го врати студентот")
    void testFindByEmailDifferentDomain() {
        String email = "student@example.com";
        Student mockStudent = new Student();
        mockStudent.setEmail(email);
        mockStudent.setName("Екстерен Студент");

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(mockStudent));

        Student result = studentService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("Екстерен Студент", result.getName());
        verify(studentRepository).findByEmail(email);
    }

    @Test
    @DisplayName("findByEmail со валиден email со специјални карактери - треба да го врати студентот")
    void testFindByEmailSpecialCharacters() {
        String email = "student.test+123@students.finki.ukim.mk";
        Student mockStudent = new Student();
        mockStudent.setEmail(email);
        mockStudent.setName("Специјален Студент");

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(mockStudent));

        Student result = studentService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("Специјален Студент", result.getName());
        verify(studentRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Тестирање на интеракција со repository - дали се повикува точно пати")
    void testRepositoryInteractionFindByEmail() {
        String email = "test@students.finki.ukim.mk";
        Student mockStudent = new Student();
        mockStudent.setEmail(email);

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(mockStudent));

        studentService.findByEmail(email);
        studentService.findByEmail(email);

        verify(studentRepository, times(2)).findByEmail(email);
    }
}