package mk.ukim.finki.wp.repoagregator.web;

import mk.ukim.finki.wp.repoagregator.model.Project;
import mk.ukim.finki.wp.repoagregator.repository.ProfessorRepository;
import mk.ukim.finki.wp.repoagregator.repository.StudentRepository;
import mk.ukim.finki.wp.repoagregator.repository.SubjectRepository;
import mk.ukim.finki.wp.repoagregator.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProjectController {
    private final ProjectService projectService;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;

    public ProjectController(ProjectService projectService, StudentRepository studentRepository, SubjectRepository subjectRepository, ProfessorRepository professorService) {
        this.projectService = projectService;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.professorRepository = professorService;
    }

    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<Project> projects = projectService.findAll();
        model.addAttribute("projects", projects);
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
        projectService.createProject(
                name,
                description,
                repoLink,
                year,
                courseIds,
                mentorIds,
                teamMemberIds,
                "221058"
        );
        return "redirect:/projects";
    }

    @GetMapping("/projects/create")
    public String getAdd(Model model) {
        model.addAttribute("availableCourses", subjectRepository.findAll());
        model.addAttribute("availableMentors", professorRepository.findAll());
        model.addAttribute("availableStudents", studentRepository.findAll());

        return "create-project";
    }
}
