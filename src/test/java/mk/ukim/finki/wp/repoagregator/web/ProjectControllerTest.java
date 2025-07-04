package mk.ukim.finki.wp.repoagregator.web;

import mk.ukim.finki.wp.repoagregator.model.*;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RepositoryType;
import mk.ukim.finki.wp.repoagregator.model.enumerations.UserRole;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProfessorTitle;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProjectNotFoundException;
import mk.ukim.finki.wp.repoagregator.service.*;
import mk.ukim.finki.wp.repoagregator.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@WebMvcTest(ProjectController.class)
@Import(ProjectControllerTest.MockConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private GithubService githubService;
    @Autowired
    private GitLabService gitLabService;
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ProfessorService professorService;
    @Autowired
    private ApprovalCommentService approvalCommentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    private Student testStudent;
    private Professor testProfessor;
    private User testStudentUser;
    private User testProfessorUser;
    private Project testProject;
    private Subject testSubject;
    private ApprovalComment testComment;

    @TestConfiguration
    static class MockConfig {
        @Bean ProjectService projectService() { return mock(ProjectService.class); }
        @Bean GithubService githubService() { return mock(GithubService.class); }
        @Bean GitLabService gitLabService() { return mock(GitLabService.class); }
        @Bean UserService userService() { return mock(UserService.class); }
        @Bean StudentService studentService() { return mock(StudentService.class); }
        @Bean ProfessorService professorService() { return mock(ProfessorService.class); }
        @Bean ApprovalCommentService approvalCommentService() { return mock(ApprovalCommentService.class); }
        @Bean StudentRepository studentRepository() { return mock(StudentRepository.class); }
        @Bean SubjectRepository subjectRepository() { return mock(SubjectRepository.class); }
    }

    @BeforeEach
    void setUp() {
        reset(projectService, githubService, gitLabService, userService,
                studentService, professorService, approvalCommentService,
                studentRepository, subjectRepository);

        testStudent = new Student("12345", "kirovski@test.com", "Милош", "Кировски", "Родител", null);
        testProfessor = new Professor("Ѓорѓи Маџаров", "madjarov@test.com", ProfessorTitle.PROFESSOR);
        testProfessor.setId("prof-123");

        testStudentUser = new User("student123", "Милош Кировски", "kirovski@test.com", UserRole.STUDENT);
        testProfessorUser = new User("prof123", "Ѓорѓи Маџаров", "madjarov@test.com", UserRole.PROFESSOR);

        testSubject = new Subject("VP", "Веб Програмирање", "VP", null, 3, 2, 2);

        testProject = new Project(
                "Тестирање Проект",
                "Опис на проект",
                "https://github.com/test/repo",
                ProjectStatus.APPROVED,
                RepositoryType.GITHUB,
                null,
                2024,
                Arrays.asList(testStudent),
                Arrays.asList(testProfessor),
                Arrays.asList(testSubject),
                testStudent
        );
        testProject.setId(1L);

        testComment = new ApprovalComment("Добар проект", true, testProject, testProfessor);
        testComment.setId(1L);
    }

    @Nested
    @DisplayName("GET /projects - Главна страница за проекти")
    class GetProjectsTests {
        @ParameterizedTest
        @MethodSource("provideProjectFilterParams")
        @WithMockUser(username = "testuser")
        void shouldHandleSearchAndFilterParametersParametrized(String search, String course, Integer year, int pageNum, int results) throws Exception {
            Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject));

            when(projectService.findPage(
                    (search == null || search.isEmpty()) ? null : search,
                    (course == null || course.isEmpty()) ? null : course,
                    year,
                    pageNum,
                    results))
                    .thenReturn(projectPage);

            when(subjectRepository.findAll()).thenReturn(Arrays.asList(testSubject));

            var requestBuilder = get("/projects")
                    .param("pageNum", String.valueOf(pageNum))
                    .param("results", String.valueOf(results));

            if (search != null && !search.isEmpty()) {
                requestBuilder.param("search", search);
            }

            if (course != null && !course.isEmpty()) {
                requestBuilder.param("course", course);
            }

            if (year != null) {
                requestBuilder.param("year", String.valueOf(year));
            }

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(view().name("projects"))
                    .andExpect(model().attributeExists("page"))
                    .andExpect(model().attribute("page", hasProperty("content", hasItem(testProject))));

            verify(projectService).findPage(
                    (search == null || search.isEmpty()) ? null : search,
                    (course == null || course.isEmpty()) ? null : course,
                    year,
                    pageNum,
                    results);
        }

        static Stream<Arguments> provideProjectFilterParams() {
            return Stream.of(
                    Arguments.of(null, null, null, 1, 5),
                    Arguments.of("Тест проект", "VP", 2024, 2, 10),
                    Arguments.of("Втор тест проект", null, 2023, 1, 5),
                    Arguments.of("Трет тест проект", "VP", null, 1, 5)
            );
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Тест за патека /")
        void shouldWorkWithRootPath() throws Exception {
            Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject));
            when(projectService.findPage(null, null, null, 1, 5))
                    .thenReturn(projectPage);
            when(subjectRepository.findAll()).thenReturn(Arrays.asList(testSubject));

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("projects"));
        }
    }

    @Nested
    @DisplayName("GET /user/{username} - Проекти по корисник")
    class GetUserProjectsTests {

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Прикажување проекти за секој студент поединечно (кои самиот студент ги креирал)")
        void shouldDisplayStudentProjects() throws Exception {
            when(userService.findById("student123")).thenReturn(testStudentUser);
            when(studentService.findByEmail("kirovski@test.com")).thenReturn(testStudent);
            when(projectService.findAllByStudent(testStudent))
                    .thenReturn(Arrays.asList(testProject));

            mockMvc.perform(get("/user/student123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("my-projects-no-filter"))
                    .andExpect(model().attributeExists("projects"))
                    .andExpect(model().attributeExists("projectStatuses"))
                    .andExpect(model().attribute("isProfessor", false))
                    .andExpect(model().attribute("repositoryTypeGithub", RepositoryType.GITHUB));

            verify(userService).findById("student123");
            verify(studentService).findByEmail("kirovski@test.com");
            verify(projectService).findAllByStudent(testStudent);
        }

        @Test
        @WithMockUser(username = "prof123")
        @DisplayName("Прикажување проекти за професори на кои тие се ментори")
        void shouldDisplayProfessorProjects() throws Exception {
            when(userService.findById("prof123")).thenReturn(testProfessorUser);
            when(projectService.findAllByMentor("prof123"))
                    .thenReturn(Arrays.asList(testProject));

            mockMvc.perform(get("/user/prof123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("my-projects-no-filter"))
                    .andExpect(model().attributeExists("projects"))
                    .andExpect(model().attribute("isProfessor", true));

            verify(userService).findById("prof123");
            verify(projectService).findAllByMentor("prof123");
        }
    }

    @Nested
    @DisplayName("GET /projects/create - Форма за креирање на проект")
    class GetCreateProjectTests {

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Прикажување форма за креирање на проект")
        void shouldDisplayCreateProjectForm() throws Exception {
            when(subjectRepository.findAll()).thenReturn(Arrays.asList(testSubject));
            when(professorService.findAll()).thenReturn(Arrays.asList(testProfessor));
            when(studentRepository.findAll()).thenReturn(Arrays.asList(testStudent));

            mockMvc.perform(get("/projects/create"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("create-project"))
                    .andExpect(model().attributeExists("availableCourses"))
                    .andExpect(model().attributeExists("availableMentors"))
                    .andExpect(model().attributeExists("availableStudents"))
                    .andExpect(model().attribute("editMode", false))
                    .andExpect(model().attribute("username", "student123"));

            verify(subjectRepository).findAll();
            verify(professorService).findAll();
            verify(studentRepository).findAll();
        }

    }

    @Nested
    @DisplayName("POST /projects/create - Креирање на проект")
    class PostCreateProjectTests {

        @ParameterizedTest
        @WithMockUser(username = "student123")
        @DisplayName("Успешно креирање на проект")
        @MethodSource("projectCreationTestData")
        void shouldCreateProjectSuccessfully(String name, String description, String repoLink,
                                             String year, String courseIds, String mentorIds,
                                             String teamMemberIds, String expectedName,
                                             String expectedDescription, String expectedRepoLink,
                                             int expectedYear) throws Exception {
            when(userService.findById("student123")).thenReturn(testStudentUser);
            when(studentService.findByEmail("kirovski@test.com")).thenReturn(testStudent);
            when(projectService.createProject(anyString(), anyString(), anyString(),
                    anyInt(), anyList(), anyList(), anyList(), anyString()))
                    .thenReturn(testProject);

            MockHttpServletRequestBuilder requestBuilder = post("/projects/create")
                    .with(csrf())
                    .param("name", name)
                    .param("year", year);

            if (description != null) requestBuilder.param("description", description);
            if (repoLink != null) requestBuilder.param("repoLink", repoLink);
            if (courseIds != null) requestBuilder.param("courseIds", courseIds);
            if (mentorIds != null) requestBuilder.param("mentorIds", mentorIds);
            if (teamMemberIds != null) requestBuilder.param("teamMemberIds", teamMemberIds);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/projects"));

            verify(userService).findById("student123");
            verify(studentService).findByEmail("kirovski@test.com");
            verify(projectService).createProject(
                    eq(expectedName),
                    expectedDescription != null ? eq(expectedDescription) : isNull(),
                    expectedRepoLink != null ? eq(expectedRepoLink) : isNull(),
                    eq(expectedYear),
                    courseIds != null ? anyList() : isNull(),
                    mentorIds != null ? anyList() : isNull(),
                    teamMemberIds != null ? anyList() : isNull(),
                    eq("12345")
            );
        }

        private static Stream<Arguments> projectCreationTestData() {
            return Stream.of(
                    Arguments.of(
                            "Тест проект", "Опис на проект", "https://github.com/test/repo",
                            "2024", "VP", "prof-123", "12345",
                            "Тест проект", "Опис на проект", "https://github.com/test/repo", 2024
                    ),
                    Arguments.of(
                            "Минимален проект", null, null,
                            "2024", null, null, null,
                            "Минимален проект", null, null, 2024
                    ),
                    Arguments.of(
                            "Проект со опис", "Само опис", null,
                            "2023", null, null, null,
                            "Проект со опис", "Само опис", null, 2023
                    ),
                    Arguments.of(
                            "Проект со репо", null, "https://gitlab.com/test/project",
                            "2025", null, null, null,
                            "Проект со репо", null, "https://gitlab.com/test/project", 2025
                    )
            );
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Неуспешно прикажување на непотврден проект во листата")
        void shouldNotDisplayUnapprovedProjectInListing() throws Exception {
            Project unapprovedProject = new Project();
            unapprovedProject.setId(42L);
            unapprovedProject.setName("Неодобрен проект");
            unapprovedProject.setProjectStatus(ProjectStatus.APPROVED);

            Page<Project> projectPage = new PageImpl<>(List.of(testProject));
            when(projectService.findPage(null, null, null, 1, 5))
                    .thenReturn(projectPage);
            when(subjectRepository.findAll()).thenReturn(Arrays.asList(testSubject));

            mockMvc.perform(get("/projects"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("projects"))
                    .andExpect(model().attributeExists("page"))
                    .andExpect(model().attributeExists("allCourses"))
                    .andExpect(model().attribute("repositoryTypeGithub", RepositoryType.GITHUB))
                    .andExpect(model().attribute("repositoryTypeGitlab", RepositoryType.GITLAB))
                    .andExpect(model().attribute("username", "testuser"))
                    .andExpect(model().attribute("page", hasProperty("content", not(hasItem(unapprovedProject)))));

            verify(projectService).findPage(null, null, null, 1, 5);
            verify(subjectRepository).findAll();
        }
    }

    @Nested
    @DisplayName("GET /projects/edit/{id} - Форма за едитирање на проект")
    class GetEditProjectTests {

        @Test
        @WithMockUser(username = "prof123")
        @DisplayName("Прикажување форма за едитирање на проект")
        void shouldDisplayEditProjectForm() throws Exception {
            when(projectService.findById(1L)).thenReturn(testProject);
            when(subjectRepository.findAll()).thenReturn(Arrays.asList(testSubject));
            when(professorService.findAll()).thenReturn(Arrays.asList(testProfessor));
            when(studentRepository.findAll()).thenReturn(Arrays.asList(testStudent));

            mockMvc.perform(get("/projects/edit/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("create-project"))
                    .andExpect(model().attribute("project", testProject))
                    .andExpect(model().attribute("editMode", true))
                    .andExpect(model().attribute("username", "prof123"));

            verify(projectService).findById(1L);
        }
    }

    @Nested
    @DisplayName("POST /projects/edit/{id} - Менување на проект")
    class PostEditProjectTests {

        @Test
        @WithMockUser(username = "prof123")
        @DisplayName("Успешно менување проект")
        void shouldUpdateProjectSuccessfully() throws Exception {
            when(projectService.findById(1L)).thenReturn(testProject);
            when(projectService.update(anyLong(), anyString(), anyString(), anyString(),
                    anyInt(), isNull(), isNull(), isNull(), anyString()))
                    .thenReturn(java.util.Optional.of(testProject));

            mockMvc.perform(post("/projects/edit/1")
                            .with(csrf())
                            .param("name", "Сменет проект")
                            .param("year", "2024")
                            .param("description", "Сменет опис")
                            .param("repoLink", "https://github.com/updated/repo"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/prof123"));

            verify(projectService).findById(1L);
            verify(projectService).update(eq(1L), eq("Сменет проект"),
                    eq("Сменет опис"), eq("https://github.com/updated/repo"),
                    eq(2024), isNull(), isNull(), isNull(), eq("12345"));
        }
    }

    @Nested
    @DisplayName("POST /projects/{id}/update-status - Менување на статус на проект")
    class UpdateProjectStatusTests {

        @Test
        @WithMockUser(username = "prof123")
        @DisplayName("Одобрување на даден проект")
        void shouldApproveProjectSuccessfully() throws Exception {
            when(professorService.getProfessorById("prof123")).thenReturn(testProfessor);
            when(projectService.findById(1L)).thenReturn(testProject);
            when(approvalCommentService.save(any(ApprovalComment.class))).thenReturn(testComment);
            when(projectService.update(eq(1L), eq(ProjectStatus.APPROVED), any(ApprovalComment.class)))
                    .thenReturn(testProject);

            mockMvc.perform(post("/projects/1/update-status")
                            .with(csrf())
                            .param("status", "APPROVED")
                            .param("comment", "Одличен проект!"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/prof123"));

            verify(professorService).getProfessorById("prof123");
            verify(projectService).findById(1L);
            verify(approvalCommentService).save(any(ApprovalComment.class));
            verify(projectService).update(eq(1L), eq(ProjectStatus.APPROVED), any(ApprovalComment.class));
        }

        @Test
        @WithMockUser(username = "prof123")
        @DisplayName("Одбивање на проект")
        void shouldRejectProjectSuccessfully() throws Exception {
            when(professorService.getProfessorById("prof123")).thenReturn(testProfessor);
            when(projectService.findById(1L)).thenReturn(testProject);
            when(approvalCommentService.save(any(ApprovalComment.class))).thenReturn(testComment);
            when(projectService.update(eq(1L), eq(ProjectStatus.REJECTED), any(ApprovalComment.class)))
                    .thenReturn(testProject);

            mockMvc.perform(post("/projects/1/update-status")
                            .with(csrf())
                            .param("status", "REJECTED")
                            .param("comment", "Needs improvement"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/prof123"));

            verify(projectService).update(eq(1L), eq(ProjectStatus.REJECTED), any(ApprovalComment.class));
        }
    }

    @Nested
    @DisplayName("GET /projects/{id} - Детали за проект")
    class GetProjectDetailsTests {

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Прикажување на детали за Github проект")
        void shouldDisplayGitHubProjectDetails() throws Exception {
            when(projectService.findById(1L)).thenReturn(testProject);
            when(githubService.fetchReadmeContent("https://github.com/test/repo"))
                    .thenReturn("# README");

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("project-details"))
                    .andExpect(model().attribute("project", testProject))
                    .andExpect(model().attribute("readme", "# README"))
                    .andExpect(model().attribute("username", "student123"));

            verify(projectService).findById(1L);
            verify(githubService).fetchReadmeContent("https://github.com/test/repo");
        }

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Прикажување на детали за Gitlab проект")
        void shouldDisplayGitLabProjectDetails() throws Exception {
            testProject.setPlatform(RepositoryType.GITLAB);
            when(projectService.findById(1L)).thenReturn(testProject);
            when(gitLabService.fetchReadmeContent("https://github.com/test/repo"))
                    .thenReturn("# README");

            mockMvc.perform(get("/projects/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("project-details"))
                    .andExpect(model().attribute("project", testProject))
                    .andExpect(model().attribute("readme", "# README"))
                    .andExpect(model().attribute("username", "student123"));

            verify(projectService).findById(1L);
            verify(gitLabService).fetchReadmeContent("https://github.com/test/repo");
        }

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Справување со параметарот fromMyProjects")
        void shouldHandleFromMyProjectsParameter() throws Exception {
            when(projectService.findById(1L)).thenReturn(testProject);
            when(githubService.fetchReadmeContent(anyString())).thenReturn("# README");
            when(approvalCommentService.findByProject(testProject)).thenReturn(testComment);

            mockMvc.perform(get("/projects/1")
                            .param("fromMyProjects", "true"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("comment"))
                    .andExpect(model().attribute("comment", testComment));

            verify(approvalCommentService).findByProject(testProject);
        }
    }

    @Nested
    @DisplayName("GET /projects/delete/{id} - Бришење на проект")
    class DeleteProjectTests {

        @Test
        @WithMockUser(username = "prof123")
        @DisplayName("Бришење на проект")
        void shouldDeleteProjectSuccessfully() throws Exception {
            doNothing().when(projectService).deleteProject(1L);

            mockMvc.perform(get("/projects/delete/1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/prof123"));

            verify(projectService).deleteProject(1L);
        }
    }

    @Nested
    @DisplayName("Тестови за безбедност")
    class SecurityTests {

        @Test
        @DisplayName("Потребна автентикација за заштитени делови од апликацијата")
        void shouldRequireAuthenticationForProtectedEndpoints() throws Exception {
            mockMvc.perform(get("/projects/create"))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(post("/projects/create").with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Барање на CSRF токен за POST барања")
        void shouldRequireCSRFTokenForPostRequests() throws Exception {
            mockMvc.perform(post("/projects/create")
                            .param("name", "Тест")
                            .param("year", "2024"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Тест за справување со грешки")
    class ErrorHandlingTests {

        @Test
        @WithMockUser(username = "student123")
        @DisplayName("Справување со непостоечки проект")
        void shouldHandleNonExistentProject() throws Exception {
            when(projectService.findById(999L)).thenThrow(new ProjectNotFoundException());

            mockMvc.perform(get("/projects/999"))
                    .andExpect(status().is4xxClientError())
                    .andExpect(view().name("error-404"))
                    .andExpect(model().attributeExists("errorMessage"))
                    .andExpect(model().attribute("errorMessage", "ГРЕШКА! Не пронајдовме проект со тоа ID..."));
        }
    }
}