package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProfessorTitle;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RepositoryType;
import mk.ukim.finki.wp.repoagregator.model.enumerations.SemesterType;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProfessorNotFoundException;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProjectNotFoundException;
import mk.ukim.finki.wp.repoagregator.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import mk.ukim.finki.wp.repoagregator.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectServiceImplTest {

    private ProjectService projectService;

    private ProjectRepository projectRepository;
    private SubjectRepository subjectRepository;
    private ProfessorRepository professorRepository;
    private StudentRepository studentRepository;

    private Student testStudent;

    private Page<Project> mockPage;


    @TestConfiguration
    static class MockConfig {
        @Bean ProjectRepository projectRepository() { return mock(ProjectRepository.class); }
        @Bean SubjectRepository subjectRepository() { return mock(SubjectRepository.class); }
        @Bean ProfessorRepository professorRepository() { return mock(ProfessorRepository.class); }
        @Bean StudentRepository studentRepository() { return mock(StudentRepository.class); }
    }

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        subjectRepository = mock(SubjectRepository.class);
        professorRepository = mock(ProfessorRepository.class);
        studentRepository = mock(StudentRepository.class);
        mockPage = mock(Page.class);


        projectService = new ProjectServiceImpl(projectRepository, subjectRepository, professorRepository, studentRepository);

        testStudent = new Student();
        testStudent.setIndex("s1");

        when(studentRepository.findById("s1")).thenReturn(Optional.of(testStudent));
        when(subjectRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(professorRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(studentRepository.findAllById(any())).thenReturn(new ArrayList<>());
        when(projectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(mockPage);
    }

    @Nested
    @DisplayName("Тестови за Креирање на Проект")
    class CreateProjectTests {

        @Test
        @DisplayName("repoUrl = null — треба да биде GITHUB")
        void testRepoUrlNull() {
            Project p = projectService.createProject("Proj", "desc", null, 2024,
                    null, null, null, "s1");
            assertEquals(RepositoryType.GITHUB, p.getPlatform());
        }
        @Test
        @DisplayName("repoUrl содржи 'gitlab' — треба да биде GITLAB")
        void testRepoUrlGitlab() {
            Project p = projectService.createProject("Proj", "desc", "https://gitlab.com/repo", 2024,
                    null, null, null, "s1");
            assertEquals(RepositoryType.GITLAB, p.getPlatform());
        }
        @Test
        @DisplayName("repoUrl не содржи 'gitlab' — треба да биде GITHUB")
        void testRepoUrlGithub() {
            Project p = projectService.createProject("Proj", "desc", "https://github.com/repo", 2024,
                    null, null, null, "s1");
            assertEquals(RepositoryType.GITHUB, p.getPlatform());
        }
        @Test
        @DisplayName("repoUrl содржи random текст — треба да биде GITHUB")
        void testRepoUrlRandomText() {
            Project p = projectService.createProject("Proj", "desc", "https://hello123.com/repo", 2024,
                    null, null, null, "s1");
            assertEquals(RepositoryType.GITHUB, p.getPlatform());
        }

        @Test
        @DisplayName("Претворање на Null листи (courseId, mentorIds, teamMembers) во empty")
        void testNullListsDefaults() {
            Project p = projectService.createProject("Name", "Desc", null, 2024,
                    null, null, null, "s1");

            verify(subjectRepository).findAllById(Collections.emptyList());
            verify(professorRepository).findAllById(Collections.emptyList());
            verify(studentRepository).findAllById(Collections.emptyList());

            assertNotNull(p.getSubjects());
            assertNotNull(p.getMentors());
            assertNotNull(p.getTeamMembers());

            assertTrue(p.getSubjects().isEmpty());
            assertTrue(p.getMentors().isEmpty());
            assertTrue(p.getTeamMembers().isEmpty());
        }

        @Test
        @DisplayName("Не-null courseIds, mentorIds, teamMemberIds")
        void testNonNullLists() {
            List<String> courseIds = List.of("c1", "c2");
            List<String> mentorIds = List.of("m1");
            List<String> teamIds = List.of("t1", "t2", "t3");

            when(subjectRepository.findAllById(courseIds)).thenReturn(
                    List.of(new Subject("c1", "Course1","C1" ,SemesterType.WINTER,2,2,2),
                            new Subject("c2", "Course2","C2" ,SemesterType.WINTER,2,2,2)));
            when(professorRepository.findAllById(mentorIds)).thenReturn(
                    List.of(new Professor("m1", "Mentor1", ProfessorTitle.PROFESSOR)));

            Student s1 = new Student();
            s1.setIndex("t1");
            s1.setEmail("helo1@gmail.com");
            s1.setName("Marko");
            s1.setLastName("Ilievski");
            s1.setParentName("Marijan");

            Student s2 = new Student();
            s2.setIndex("t2");
            Student s3 = new Student();
            s3.setIndex("t3");

            when(studentRepository.findAllById(teamIds)).thenReturn(
                    List.of(s1, s2, s3)
            );
            Project p = projectService.createProject("Proekt1", "Opis", null, 2024,
                    courseIds, mentorIds, teamIds, "s1");

            verify(subjectRepository).findAllById(courseIds);
            verify(professorRepository).findAllById(mentorIds);
            verify(studentRepository).findAllById(teamIds);

            assertEquals(2, p.getSubjects().size());
            assertEquals(1, p.getMentors().size());
            assertEquals(3, p.getTeamMembers().size());
        }

        @Test
        @DisplayName("Тестирање на createProject студент кој не постои")
        void testCreatorStudentNotFound() {
            when(studentRepository.findById("invalid")).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                projectService.createProject("Proekt1", "Opis", null, 2024,
                        null, null, null, "invalid");
            });

            assertEquals("Student not found", ex.getMessage());
        }

        @Test
        @DisplayName("Тестирање дали проектот има точни податоци по зачувување")
        void testProjectFields() {
            Project p = projectService.createProject("Proekt1", "Opis", "https://github.com/repo", 2023,
                    null, null, null, "s1");

            assertEquals("Proekt1", p.getName());
            assertEquals("Opis", p.getShortDescription());
            assertEquals("https://github.com/repo", p.getRepoUrl());
            assertEquals(ProjectStatus.PENDING, p.getProjectStatus());
            assertEquals(RepositoryType.GITHUB, p.getPlatform());
            assertEquals(2023, p.getYear());
            assertEquals(testStudent, p.getCreatedBy());
        }
    }

    @Nested
    @DisplayName("Тестови за Пронаоѓање на Проекти")
    class FindProjectTests {

        @Test
        @DisplayName("Edge Coverage: findById со валиден ID")
        void testFindByIdSuccess() {
            Project mockProject = new Project();
            mockProject.setId(1L);
            mockProject.setName("Proekt Test");

            when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

            Project result = projectService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Proekt Test", result.getName());
            verify(projectRepository).findById(1L);
        }

        @Test
        @DisplayName("Edge Coverage: findById со невалиден ID")
        void testFindByIdNotFound() {
            when(projectRepository.findById(999L)).thenReturn(Optional.empty());

            ProjectNotFoundException ex = assertThrows(ProjectNotFoundException.class, () -> {
                projectService.findById(999L);
            });

            assertTrue(ex.getMessage().contains("ГРЕШКА! Не пронајдовме проект со тоа ID..."));

            verify(projectRepository).findById(999L);
        }

        @Test
        @DisplayName("Тестирање на findAll на проекти креирани од студент")
        void testFindAllByStudentWithProjects() {
            Student student = new Student();
            student.setIndex("s1");

            List<Project> mockProjects = List.of(
                    new Project(),
                    new Project()
            );

            when(projectRepository.findAllByCreatedBy(student)).thenReturn(mockProjects);
            List<Project> result = projectService.findAllByStudent(student);
            assertEquals(2, result.size());
            verify(projectRepository).findAllByCreatedBy(student);
        }

        @Test
        @DisplayName("Тестирање на findAll од студент без креирани проекти")
        void testFindAllByStudentNoProjects() {
            Student student = new Student();
            student.setIndex("s2");

            when(projectRepository.findAllByCreatedBy(student)).thenReturn(Collections.emptyList());

            List<Project> result = projectService.findAllByStudent(student);

            assertTrue(result.isEmpty());
            verify(projectRepository).findAllByCreatedBy(student);
        }

        @Test
        @DisplayName("Тестирање на findAllByMentor со валиден ментор")
        void testFindAllByMentorSuccess() {
            Professor mockProfessor = new Professor("m1", "Prof", ProfessorTitle.PROFESSOR);
            List<Project> mockProjects = List.of(new Project(), new Project());
            when(professorRepository.findByid("m1")).thenReturn(Optional.of(mockProfessor));
            when(projectRepository.findAllByMentorsContaining(mockProfessor)).thenReturn(mockProjects);
            List<Project> result = projectService.findAllByMentor("m1");

            assertEquals(2, result.size());
            verify(professorRepository).findByid("m1");
            verify(projectRepository).findAllByMentorsContaining(mockProfessor);
        }

        @Test
        @DisplayName("Тестирање на findAllByMentor со невалиден ментор")
        void testFindAllByMentorNotFound() {
            when(professorRepository.findByid("invalid")).thenReturn(Optional.empty());

            ProfessorNotFoundException ex = assertThrows(ProfessorNotFoundException.class, () -> {
                projectService.findAllByMentor("invalid");
            });

            assertTrue(ex.getMessage().contains("Професорот не е пронајден."));

            verify(professorRepository).findByid("invalid");
            verify(projectRepository, never()).findAllByMentorsContaining(any());
        }

        @Test
        @DisplayName("Тестирање на findAllByMentor со валиден ментор без проекти")
        void testFindAllByMentorNoProjects() {
            Professor mockProfessor = new Professor("m2", "Prof", ProfessorTitle.PROFESSOR);

            when(professorRepository.findByid("m2")).thenReturn(Optional.of(mockProfessor));
            when(projectRepository.findAllByMentorsContaining(mockProfessor)).thenReturn(Collections.emptyList());

            List<Project> result = projectService.findAllByMentor("m2");

            assertTrue(result.isEmpty());
            verify(professorRepository).findByid("m2");
            verify(projectRepository).findAllByMentorsContaining(mockProfessor);
        }
    }

    @Nested
    @DisplayName("Тестови за Ажурирање на Проекти")
    class UpdateProjectTests {

        @Test
        @DisplayName("Тестирање на ажурирање на статус и коментар")
        void testSimpleUpdateSuccess() {
            Project mockProject = new Project();
            mockProject.setId(1L);
            mockProject.setProjectStatus(ProjectStatus.PENDING);

            ApprovalComment comment = new ApprovalComment();
            comment.setComment("Super e!");

            when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
            when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

            Project result = projectService.update(1L, ProjectStatus.APPROVED, comment);

            assertEquals(ProjectStatus.APPROVED, result.getProjectStatus());
            assertEquals(comment, result.getApprovalComment());
            verify(projectRepository).findById(1L);
            verify(projectRepository).save(mockProject);
        }

        @Test
        @DisplayName("Тестирање на ажурирање на непостоечки проект")
        void testSimpleUpdateProjectNotFound() {
            when(projectRepository.findById(999L)).thenReturn(Optional.empty());

            ApprovalComment comment = new ApprovalComment();

            ProjectNotFoundException ex = assertThrows(ProjectNotFoundException.class, () -> {
                projectService.update(999L, ProjectStatus.REJECTED, comment);
            });

            assertTrue(ex.getMessage().contains("ГРЕШКА! Не пронајдовме проект со тоа ID..."));
            verify(projectRepository).findById(999L);
            verify(projectRepository, never()).save(any());
        }

        @Test
        @DisplayName("Tестирање на различни статуси на проект")
        void testUpdateDifferentStatuses() {
            Project mockProject = new Project();
            mockProject.setId(1L);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
            when(projectRepository.save(any())).thenReturn(mockProject);

            Project result1 = projectService.update(1L, ProjectStatus.APPROVED, null);
            assertEquals(ProjectStatus.APPROVED, result1.getProjectStatus());

            Project result2 = projectService.update(1L, ProjectStatus.REJECTED, null);
            assertEquals(ProjectStatus.REJECTED, result2.getProjectStatus());

            Project result3 = projectService.update(1L, ProjectStatus.PENDING, null);
            assertEquals(ProjectStatus.PENDING, result3.getProjectStatus());

            verify(projectRepository, times(3)).findById(1L);
            verify(projectRepository, times(3)).save(mockProject);
        }
    }

    @Nested
    @DisplayName("Тестови за Бришење на Проекти")
    class DeleteProjectTests {

        @Test
        @DisplayName("Тестирање на бришење на проект како и успешно отстранување на врски со други објекти")
        void testDeleteProjectWithAssociations() {
            Project mockProject = new Project();
            mockProject.setId(1L);

            List<Student> teamMembers = new ArrayList<>();
            teamMembers.add(new Student());
            teamMembers.add(new Student());

            List<Professor> mentors = new ArrayList<>();
            mentors.add(new Professor("p1", "Profesor", ProfessorTitle.PROFESSOR));

            List<Subject> subjects = new ArrayList<>();
            subjects.add(new Subject("p1", "Predmet", "P1", SemesterType.WINTER, 2, 2, 2));

            mockProject.setTeamMembers(teamMembers);
            mockProject.setMentors(mentors);
            mockProject.setSubjects(subjects);

            when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

            projectService.deleteProject(1L);

            verify(projectRepository).findById(1L);
            verify(projectRepository).save(mockProject);
            verify(projectRepository).deleteById(1L);
            verify(projectRepository).flush();

            assertTrue(mockProject.getTeamMembers().isEmpty());
            assertTrue(mockProject.getMentors().isEmpty());
            assertTrue(mockProject.getSubjects().isEmpty());
        }

        @Test
        @DisplayName("Тестирање на бришење на непостоечки проект")
        void testDeleteProjectNotFound() {
            when(projectRepository.findById(999L)).thenReturn(Optional.empty());

            ProjectNotFoundException ex = assertThrows(ProjectNotFoundException.class, () -> {
                projectService.deleteProject(999L);
            });

            assertTrue(ex.getMessage().contains("ГРЕШКА! Не пронајдовме проект со тоа ID..."));
            verify(projectRepository).findById(999L);
            verify(projectRepository, never()).save(any());
            verify(projectRepository, never()).deleteById(any());
            verify(projectRepository, never()).flush();
        }
    }
    @Nested
    @DisplayName("Тестови за Пагинација и Филтрирање")
    class FilterAndPagination {
        private Project p1, p2, p3, p4;

        @BeforeEach
        void initTestProjects() {
            p1 = new Project();
            p1.setName("AI Leto/2024");
            p1.setYear(2024);
            p1.setSubjects(List.of(new Subject("c1", "AI", "AI", SemesterType.SUMMER, 2, 2, 1)));
            p1.setProjectStatus(ProjectStatus.APPROVED);

            p2 = new Project();
            p2.setName("Verojatnost 2023");
            p2.setYear(2023);
            p2.setSubjects(List.of(new Subject("c2", "Verojatnost i Statistika", "ViS", SemesterType.WINTER, 2, 2, 1)));
            p2.setProjectStatus(ProjectStatus.APPROVED);

            p3 = new Project();
            p3.setName("AI Models");
            p3.setYear(2024);
            p3.setSubjects(List.of(new Subject("c3", "AI", "AI", SemesterType.WINTER, 2, 2, 1)));
            p3.setProjectStatus(ProjectStatus.PENDING);

            p4 = new Project();
            p4.setName("Mrezi 2024");
            p4.setYear(2024);
            p4.setSubjects(List.of(new Subject("c4", "Mrezi i Bezbednost", "MiB", SemesterType.WINTER, 2, 2, 1)));
            p4.setProjectStatus(ProjectStatus.APPROVED);
        }

        @Test
        @DisplayName("search != null, course != null, year != null")
        void testAllFiltersMatch_p1() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1)));

            Page<Project> result = projectService.findPage("AI", "c1", 2024, 1, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals("AI Leto/2024", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("search != null, course != null, year == null")
        void testSearchCourseOnly() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1)));

            Page<Project> result = projectService.findPage("AI", "c1", null, 1, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals("AI Leto/2024", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("search != null, course == null, year != null")
        void testSearchYearOnly() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1)));
            Page<Project> result = projectService.findPage("AI", null, 2024, 1, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals("AI Leto/2024", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("search == null, course != null, year != null")
        void testCourseYearOnly() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1)));

            Page<Project> result = projectService.findPage(null, "c1", 2024, 1, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals("AI Leto/2024", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("search == null, course == null, year != null")
        void testYearOnly() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1, p4)));

            Page<Project> result = projectService.findPage(null, null, 2024, 1, 10);

            assertEquals(2, result.getTotalElements());
            assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("AI Leto/2024")));
            assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("Mrezi 2024")));
        }

        @Test
        @DisplayName("search == null, course != null, year == null")
        void testCourseOnly() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1)));

            Page<Project> result = projectService.findPage(null, "c1", null, 1, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals("AI Leto/2024", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("search != null, course == null, year == null")
        void testSearchOnly() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1)));

            Page<Project> result = projectService.findPage("AI", null, null, 1, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals("AI Leto/2024", result.getContent().get(0).getName());
        }

        @Test
        @DisplayName("search == null, course == null, year == null")
        void testNoFilters() {
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(p1, p2, p4)));

            Page<Project> result = projectService.findPage(null, null, null, 1, 10);

            assertEquals(3, result.getTotalElements());
            assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("AI Leto/2024")));
            assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("Verojatnost 2023")));
            assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("Mrezi 2024")));
        }

        @Test
        @DisplayName("Различни големини на страна")
        void testFindPageDifferentPagination() {
            projectService.findPage(null, null, null, 1, 5);
            verify(projectRepository).findAll(any(Specification.class), eq(PageRequest.of(0, 5)));

            reset(projectRepository);
            when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(mockPage);

            projectService.findPage(null, null, null, 3, 25);
            verify(projectRepository).findAll(any(Specification.class), eq(PageRequest.of(2, 25)));
        }
    }
}

