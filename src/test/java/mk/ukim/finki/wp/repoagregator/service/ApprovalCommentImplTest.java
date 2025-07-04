package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import mk.ukim.finki.wp.repoagregator.model.Professor;
import mk.ukim.finki.wp.repoagregator.model.Project;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProfessorTitle;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.repository.ApprovalCommentRepository;
import mk.ukim.finki.wp.repoagregator.service.impl.ApprovalCommentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApprovalCommentImplTest {

    private ApprovalCommentService approvalCommentService;
    private ApprovalCommentRepository approvalCommentRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        ApprovalCommentRepository approvalCommentRepository() {
            return mock(ApprovalCommentRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        approvalCommentRepository = mock(ApprovalCommentRepository.class);
        approvalCommentService = new ApprovalCommentImpl(approvalCommentRepository);
    }

    @Nested
    @DisplayName("Тестови за зачувување на ApprovalComment")
    class SaveApprovalCommentTests {
        @Test
        @DisplayName("save со валиден ApprovalComment - треба да го зачува и врати")
        void testSaveValidApprovalComment() {
            ApprovalComment comment = new ApprovalComment();
            comment.setId(1L);
            comment.setComment("Test comment");

            when(approvalCommentRepository.save(comment)).thenReturn(comment);
            ApprovalComment result = approvalCommentService.save(comment);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test comment", result.getComment());
            verify(approvalCommentRepository).save(comment);
        }

        @Test
        @DisplayName("save со null ApprovalComment - треба да го проследи на repository")
        void testSaveNullApprovalComment() {
            ApprovalComment comment = null;
            when(approvalCommentRepository.save(comment)).thenReturn(comment);
            ApprovalComment result = approvalCommentService.save(comment);
            assertNull(result);
            verify(approvalCommentRepository).save(comment);
        }

        @Test
        @DisplayName("save со празен ApprovalComment - треба да го зачува")
        void testSaveEmptyApprovalComment() {
            ApprovalComment comment = new ApprovalComment();
            when(approvalCommentRepository.save(comment)).thenReturn(comment);

            ApprovalComment result = approvalCommentService.save(comment);

            assertNotNull(result);
            assertSame(comment, result);
            verify(approvalCommentRepository).save(comment);
        }

        @Test
        @DisplayName("save со ApprovalComment со сите полиња - треба да ги зачува сите")
        void testSaveCompleteApprovalComment() {
            Project project = new Project();
            project.setId(1L);
            project.setName("Test Project");
            project.setProjectStatus(ProjectStatus.PENDING);

            Professor professor = new Professor();
            professor.setId("1L");
            professor.setName("Test Professor");
            professor.setTitle(ProfessorTitle.PROFESSOR);

            ApprovalComment comment = new ApprovalComment();
            comment.setId(1L);
            comment.setComment("Moze da go pocnete proektot :)");
            comment.setProject(project);
            comment.setApproved(true);
            comment.setReviewer(professor);

            when(approvalCommentRepository.save(comment)).thenReturn(comment);

            ApprovalComment result = approvalCommentService.save(comment);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Moze da go pocnete proektot :)", result.getComment());
            assertNotNull(result.getProject());
            assertEquals(1L, result.getProject().getId());
            assertEquals("Test Project", result.getProject().getName());
            assertNotNull(result.getReviewer());
            assertEquals("1L", result.getReviewer().getId());
            assertEquals("Test Professor", result.getReviewer().getName());

            verify(approvalCommentRepository).save(comment);
        }
    }

    @Nested
    @DisplayName("Тестови за пронаоѓање на ApprovalComment по Project")
    class FindByProjectTests {
        @Test
        @DisplayName("findByProject со валиден проект - треба да го врати коментарот")
        void testFindByProjectValidProject() {
            Project project = new Project();
            project.setId(1L);
            project.setName("Test Project");

            ApprovalComment comment = new ApprovalComment();
            comment.setId(1L);
            comment.setComment("Test comment");
            comment.setProject(project);

            when(approvalCommentRepository.findByProject(project)).thenReturn(comment);
            ApprovalComment result = approvalCommentService.findByProject(project);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test comment", result.getComment());
            assertEquals(project, result.getProject());
            verify(approvalCommentRepository).findByProject(project);
        }

        @Test
        @DisplayName("findByProject со проект кој нема коментар - треба да врати null")
        void testFindByProjectNoComment() {
            Project project = new Project();
            project.setId(1L);
            project.setName("Test Project");

            when(approvalCommentRepository.findByProject(project)).thenReturn(null);

            ApprovalComment result = approvalCommentService.findByProject(project);
            assertNull(result);
            verify(approvalCommentRepository).findByProject(project);
        }

        @Test
        @DisplayName("findByProject со null проект - треба да го проследи на repository")
        void testFindByProjectNullProject() {
            Project project = null;
            when(approvalCommentRepository.findByProject(project)).thenReturn(null);

            ApprovalComment result = approvalCommentService.findByProject(project);

            assertNull(result);
            verify(approvalCommentRepository).findByProject(project);
        }

        @Test
        @DisplayName("findByProject со празен проект - треба да работи нормално")
        void testFindByProjectEmptyProject() {
            Project project = new Project();
            ApprovalComment comment = new ApprovalComment();
            comment.setProject(project);

            when(approvalCommentRepository.findByProject(project)).thenReturn(comment);

            ApprovalComment result = approvalCommentService.findByProject(project);

            assertNotNull(result);
            assertEquals(project, result.getProject());
            verify(approvalCommentRepository).findByProject(project);
        }
        @Test
        @DisplayName("findByProject со различни проекти - треба да врати различни коментари")
        void testFindByProjectDifferentProjects() {
            Project project1 = new Project();
            project1.setId(1L);
            project1.setName("Project 1");

            Project project2 = new Project();
            project2.setId(2L);
            project2.setName("Project 2");

            ApprovalComment comment1 = new ApprovalComment();
            comment1.setId(1L);
            comment1.setComment("Comment 1");
            comment1.setProject(project1);

            ApprovalComment comment2 = new ApprovalComment();
            comment2.setId(2L);
            comment2.setComment("Comment 2");
            comment2.setProject(project2);

            when(approvalCommentRepository.findByProject(project1)).thenReturn(comment1);
            when(approvalCommentRepository.findByProject(project2)).thenReturn(comment2);

            ApprovalComment result1 = approvalCommentService.findByProject(project1);
            ApprovalComment result2 = approvalCommentService.findByProject(project2);

            assertNotNull(result1);
            assertNotNull(result2);
            assertNotEquals(result1.getId(), result2.getId());
            assertEquals("Comment 1", result1.getComment());
            assertEquals("Comment 2", result2.getComment());
            verify(approvalCommentRepository).findByProject(project1);
            verify(approvalCommentRepository).findByProject(project2);
        }
    }
}
