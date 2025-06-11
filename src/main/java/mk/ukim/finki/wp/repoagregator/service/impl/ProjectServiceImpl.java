package mk.ukim.finki.wp.repoagregator.service.impl;


import jakarta.transaction.Transactional;
import mk.ukim.finki.wp.repoagregator.model.*;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RepositoryType;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProjectNotFoundException;
import mk.ukim.finki.wp.repoagregator.repository.*;
import mk.ukim.finki.wp.repoagregator.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

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

        Project project = new Project(name, description, repoUrl, ProjectStatus.PENDING, RepositoryType.GITHUB, null, year, teamMembers, mentors, subjects, creator);
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
}
