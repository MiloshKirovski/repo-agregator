package mk.ukim.finki.wp.repoagregator.web;

import jakarta.servlet.http.HttpServletResponse;
import mk.ukim.finki.wp.repoagregator.model.*;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RepositoryType;
import mk.ukim.finki.wp.repoagregator.model.enumerations.UserRole;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProjectNotFoundException;
import mk.ukim.finki.wp.repoagregator.repository.StudentRepository;
import mk.ukim.finki.wp.repoagregator.repository.SubjectRepository;
import mk.ukim.finki.wp.repoagregator.service.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProjectController {
    private final ProjectService projectService;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final GithubService githubService;
    private final GitLabService gitLabService;
    private final UserService userService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final ApprovalCommentService approvalCommentService;

    public ProjectController(ProjectService projectService, StudentRepository studentRepository, SubjectRepository subjectRepository, GithubService githubService, GitLabService gitLabService, UserService userService, StudentService studentService, ProfessorService professorService, ApprovalCommentService approvalCommentService) {
        this.projectService = projectService;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.githubService = githubService;
        this.gitLabService = gitLabService;
        this.userService = userService;
        this.studentService = studentService;
        this.professorService = professorService;
        this.approvalCommentService = approvalCommentService;
    }

    @GetMapping(value = {"/", "/projects"})
    public String getProjects(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String course,
                              @RequestParam(required = false) Integer year,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "5") Integer results,
                              Model model) {
        Page<Project> page = this.projectService.findPage(search, course, year, pageNum, results);
        model.addAttribute("page", page);
        model.addAttribute("allCourses", subjectRepository.findAll());
        model.addAttribute("year", year);
        model.addAttribute("search", search);
        model.addAttribute("course", course);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("repositoryTypeGithub", RepositoryType.GITHUB);
        model.addAttribute("repositoryTypeGitlab", RepositoryType.GITLAB);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "projects";
    }

    @PostMapping("/projects/create")
    public String createProject(
            @RequestParam String name,
            @RequestParam Integer year,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> courseIds,
            @RequestParam(required = false) List<String> mentorIds,
            @RequestParam(required = false) List<String> teamMemberIds,
            @RequestParam(required = false) String repoLink
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        String userEmail = userService.findById(currentUserId).getEmail();
        String studentId = studentService.findByEmail(userEmail).getIndex();

        projectService.createProject(
                name,
                description,
                repoLink,
                year,
                courseIds,
                mentorIds,
                teamMemberIds,
                studentId
        );
        return "redirect:/projects";
    }

    @GetMapping("/user/{username}")
    public String getMyProjectsPage(@PathVariable String username,
                                    Model model) {
        User user = userService.findById(username);
        boolean isStudent = user.getRole() == UserRole.STUDENT;
        boolean isProfessor = user.getRole() == UserRole.PROFESSOR || user.getRole() == UserRole.ACADEMIC_AFFAIR_VICE_DEAN;

        List<Project> projects;

        if (isStudent) {
            String email = user.getEmail();
            Student student = studentService.findByEmail(email);
            projects = projectService.findAllByStudent(student);
        } else if (isProfessor) {
            projects = projectService.findAllByMentor(username);
        } else {
            projects = List.of();
        }

        model.addAttribute("projectStatuses", ProjectStatus.values());
        model.addAttribute("projects", projects);
        model.addAttribute("repositoryTypeGithub", RepositoryType.GITHUB);
        model.addAttribute("repositoryTypeGitlab", RepositoryType.GITLAB);
        model.addAttribute("isProfessor", isProfessor);

        return "my-projects-no-filter";
    }

    @PostMapping("/projects/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ProjectStatus status,
                               @RequestParam String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName();
        Professor professor = professorService.getProfessorById(currentUserId);

        Project project = projectService.findById(id);

        boolean approved = status.equals(ProjectStatus.APPROVED);
        ApprovalComment approvalComment = new ApprovalComment(comment, approved, project, professor);

        approvalCommentService.save(approvalComment);
        projectService.update(id, status, approvalComment);

        return "redirect:/user/" + currentUserId;
    }

    @GetMapping("/projects/create")
    public String getAdd(Model model) {
        model.addAttribute("availableCourses", subjectRepository.findAll());
        model.addAttribute("availableMentors", professorService.findAll());
        model.addAttribute("availableStudents", studentRepository.findAll());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());

        model.addAttribute("editMode", false);
        return "create-project";
    }

    @GetMapping("/projects/edit/{id}")
    public String getEdit(@PathVariable Long id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);

        model.addAttribute("availableCourses", subjectRepository.findAll());
        model.addAttribute("availableMentors", professorService.findAll());
        model.addAttribute("availableStudents", studentRepository.findAll());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("editMode", true);

        return "create-project";
    }

    @PostMapping("/projects/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam Integer year,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false) List<String> courseIds,
                         @RequestParam(required = false) List<String> mentorIds,
                         @RequestParam(required = false) List<String> teamMemberIds,
                         @RequestParam(required = false) String repoLink) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Project project = projectService.findById(id);
        String studentId = project.getCreatedBy().getIndex();

        projectService.update(
                id,
                name,
                description,
                repoLink,
                year,
                courseIds,
                mentorIds,
                teamMemberIds,
                studentId
        );

        String username = auth.getName();
        return "redirect:/user/" + username;
    }

    @GetMapping("/projects/{id}")
    public String viewProjectDetails(@PathVariable Long id,
                                     Model model,
                                     @RequestParam(required = false) String fromMyProjects,
                                     HttpServletResponse response) {
        try {
            Project project = projectService.findById(id);
            String readmeContent = "";
            if (project.getPlatform().equals(RepositoryType.GITHUB)) {
                readmeContent = githubService.fetchReadmeContent(project.getRepoUrl());
            } else if (project.getPlatform().equals(RepositoryType.GITLAB)) {
                readmeContent = gitLabService.fetchReadmeContent(project.getRepoUrl());
            }

            if (fromMyProjects != null) {
                ApprovalComment comment = approvalCommentService.findByProject(project);
                model.addAttribute("comment", comment);
            } else {
                model.addAttribute("comment", new ApprovalComment("", false, null, null));
            }

            model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
            model.addAttribute("project", project);
            model.addAttribute("readme", readmeContent);
            model.addAttribute("repositoryTypeGithub", RepositoryType.GITHUB);
            model.addAttribute("repositoryTypeGitlab", RepositoryType.GITLAB);

            return "project-details";

        } catch (ProjectNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "error-404";
        }
    }


    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return "redirect:/user/" + username;
    }
}
