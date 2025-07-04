package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.Professor;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProfessorNotFoundException;
import mk.ukim.finki.wp.repoagregator.repository.ProfessorRepository;
import mk.ukim.finki.wp.repoagregator.service.impl.ProfessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfessorServiceImplTest {

    private ProfessorService professorService;
    private ProfessorRepository professorRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        ProfessorRepository professorRepository() {
            return mock(ProfessorRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        professorRepository = mock(ProfessorRepository.class);
        professorService = new ProfessorServiceImpl(professorRepository);
    }

    @Test
    @DisplayName("getProfessorById со валиден ID - треба да го врати професорот")
    void testGetProfessorByIdSuccess() {
        String professorId = "prof123";
        Professor mockProfessor = new Professor();
        mockProfessor.setId(professorId);
        mockProfessor.setName("Test Prof");

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        Professor result = professorService.getProfessorById(professorId);

        assertNotNull(result);
        assertEquals(professorId, result.getId());
        assertEquals("Test Prof", result.getName());
        verify(professorRepository).findById(professorId);
    }

    @Test
    @DisplayName("getProfessorById со невалиден ID - треба да фрли ProfessorNotFoundException")
    void testGetProfessorByIdNotFound() {
        String professorId = "nevalidenprof";

        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        ProfessorNotFoundException exception = assertThrows(ProfessorNotFoundException.class, () -> {
            professorService.getProfessorById(professorId);
        });

        assertEquals("Professor with id " + professorId + " doesn't exist", exception.getMessage());
        verify(professorRepository).findById(professorId);
    }

    @Test
    @DisplayName("getProfessorById со null ID - треба да фрли ProfessorNotFoundException")
    void testGetProfessorByIdNullId() {
        String professorId = null;

        when(professorRepository.findById(professorId)).thenReturn(Optional.empty());

        ProfessorNotFoundException exception = assertThrows(ProfessorNotFoundException.class, () -> {
            professorService.getProfessorById(professorId);
        });

        assertEquals("Professor with id " + professorId + " doesn't exist", exception.getMessage());
        verify(professorRepository).findById(professorId);
    }
    @Test
    @DisplayName("findAll - треба да ги врати сите професори")
    void testFindAll() {
        Professor professor1 = new Professor();
        professor1.setId("p1");
        professor1.setName("professor1");

        Professor professor2 = new Professor();
        professor2.setId("p2");
        professor2.setName("professor2");

        Professor professor3 = new Professor();
        professor3.setId("p3");
        professor3.setName("professor3");

        List<Professor> mockProfessors = Arrays.asList(professor1, professor2, professor3);

        when(professorRepository.findAll()).thenReturn(mockProfessors);
        List<Professor> result = professorService.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(professorRepository).findAll();
    }

    @Test
    @DisplayName("findAll со празен резултат - треба да врати празна листа")
    void testFindAllEmpty() {
        when(professorRepository.findAll()).thenReturn(Arrays.asList());

        List<Professor> result = professorService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(professorRepository).findAll();
    }

    @Test
    @DisplayName("Тестирање на repository интеракција - дали се повикува точно пати")
    void testRepositoryInteractionGetProfessorById() {
        String professorId = "prof123";
        Professor mockProfessor = new Professor();
        mockProfessor.setId(professorId);

        when(professorRepository.findById(professorId)).thenReturn(Optional.of(mockProfessor));

        professorService.getProfessorById(professorId);
        professorService.getProfessorById(professorId);

        verify(professorRepository, times(2)).findById(professorId);
    }
}
