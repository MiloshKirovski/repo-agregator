package mk.ukim.finki.wp.repoagregator.service.impl;


import jakarta.transaction.Transactional;
import mk.ukim.finki.wp.repoagregator.model.*;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RepositoryType;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProjectNotFoundException;
import mk.ukim.finki.wp.repoagregator.repository.*;
import mk.ukim.finki.wp.repoagregator.service.ProjectService;
import mk.ukim.finki.wp.repoagregator.specifications.FieldFilterSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;
    private final ApprovalCommentRepository approvalCommentRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              SubjectRepository subjectRepository,
                              ProfessorRepository professorRepository,
                              StudentRepository studentRepository,
                              ApprovalCommentRepository approvalCommentRepository) {
        this.projectRepository = projectRepository;
        this.subjectRepository = subjectRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.approvalCommentRepository = approvalCommentRepository;
    }

    @Override
    @Transactional
    public Project createProject(String name,
                                 String description,
                                 String repoUrl,
                                 int year,
                                 List<String> courseIds,
                                 List<String> mentorIds,
                                 List<String> teamMemberIds,
                                 String createdByStudentId) {

        List<Subject> subjects = subjectRepository.findAllById(courseIds);
        List<Professor> mentors = professorRepository.findAllById(mentorIds);
        List<Student> teamMembers = studentRepository.findAllById(teamMemberIds);
        Student creator = studentRepository.findById(createdByStudentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        RepositoryType repoType ;
        if (repoUrl != null && repoUrl.contains("gitlab")) {
            repoType=RepositoryType.GITLAB;
        }else{
            repoType=RepositoryType.GITHUB;

        }

        Project project = new Project(name, description, repoUrl, ProjectStatus.PENDING, repoType, null, year, teamMembers, mentors, subjects, creator);
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void approveProject(Long projectId, String professorId, String comment) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setProjectStatus(ProjectStatus.APPROVED);

        Professor reviewer = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        ApprovalComment approvalComment = new ApprovalComment(comment, true, project, reviewer);

        approvalCommentRepository.save(approvalComment);
        project.setApprovalComment(approvalComment);

        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void rejectProject(Long projectId, String professorId, String comment) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setProjectStatus(ProjectStatus.REJECTED);

        Professor reviewer = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        ApprovalComment approvalComment = new ApprovalComment(comment, false, project, reviewer);

        approvalCommentRepository.save(approvalComment);
        project.setApprovalComment(approvalComment);

        projectRepository.save(project);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
    }

    public List<Project> findAllApproved() {
        return projectRepository.findAll().stream().filter(p -> p.getProjectStatus() == ProjectStatus.APPROVED).collect(Collectors.toList());
    }

    @Override
    public List<Project> findAllByStudent(Student student) {
        return projectRepository.findAllByCreatedBy(student);
    }

    @Override
    public List<Project> findAllByMentor(String mentorId) {
        Professor professor = professorRepository.findByid(mentorId).orElseThrow(RuntimeException::new);
        System.out.println(professor.getId());
        return projectRepository.findAllByMentorsContaining(professor);
    }

    @Override
    public Project update(Long projectId, ProjectStatus projectStatus, ApprovalComment approvalComment) {
        Project projectMain =  projectRepository.findById(projectId).orElseThrow(RuntimeException::new);
        projectMain.setProjectStatus(projectStatus);
        projectMain.setApprovalComment(approvalComment);
        return projectRepository.save(projectMain);
    }

    @Override
    public Page<Project> findPage(String search, String course, Integer year, Integer pageNum, Integer pageSize) {
        System.out.println("=== DEBUGGING FILTERS ===");
        System.out.println("Search: '" + search + "' (length: " + (search != null ? search.length() : "null") + ")");
        System.out.println("Course: '" + course + "'");
        System.out.println("Year: " + year);

        // Create specifications
        Specification<Project> searchSpec = FieldFilterSpecification.filterContainsText(Project.class, "name", search);
        Specification<Project> courseSpec = FieldFilterSpecification.filterEqualsInCollection("subjects.id", course);
        Specification<Project> yearSpec = FieldFilterSpecification.filterEqualsV(Project.class, "year", year);
        Specification<Project> projectStatus = FieldFilterSpecification.filterEqualsV(Project.class, "projectStatus", ProjectStatus.APPROVED);

        System.out.println("Search spec created: " + (searchSpec != null));
        System.out.println("Course spec created: " + (courseSpec != null));
        System.out.println("Year spec created: " + (yearSpec != null));

        // Test search filter alone first
        if (searchSpec != null) {
            try {
                Page<Project> searchResults = this.projectRepository.findAll(
                        searchSpec,
                        PageRequest.of(pageNum - 1, pageSize)
                );
                System.out.println("Search-only results: " + searchResults.getTotalElements());
            } catch (Exception e) {
                System.out.println("Error with search filter: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Combine all specifications
        Specification<Project> specification = Specification
                .where(searchSpec)
                .and(courseSpec)
                .and(yearSpec)
                .and(projectStatus);


        try {
            Page<Project> finalResults = this.projectRepository.findAll(
                    specification,
                    PageRequest.of(pageNum - 1, pageSize)
            );
            System.out.println("Final combined results: " + finalResults.getTotalElements());
            System.out.println("=== END DEBUG ===");
            return finalResults;
        } catch (Exception e) {
            System.out.println("Error with combined specification: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== END DEBUG ===");
            throw e;
        }
    }


}
