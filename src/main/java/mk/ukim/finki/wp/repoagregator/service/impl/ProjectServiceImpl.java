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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              SubjectRepository subjectRepository,
                              ProfessorRepository professorRepository,
                              StudentRepository studentRepository) {
        this.projectRepository = projectRepository;
        this.subjectRepository = subjectRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
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
    public Project findById(Long id) {
        return projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
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
        Specification<Project> searchSpec = FieldFilterSpecification.filterContainsText(Project.class, "name", search);
        Specification<Project> courseSpec = FieldFilterSpecification.filterEqualsInCollection("subjects.id", course);
        Specification<Project> yearSpec = FieldFilterSpecification.filterEqualsV(Project.class, "year", year);
        Specification<Project> projectStatus = FieldFilterSpecification.filterEqualsV(Project.class, "projectStatus", ProjectStatus.APPROVED);

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
            return finalResults;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);

        project.getTeamMembers().clear();
        project.getMentors().clear();
        project.getSubjects().clear();

        projectRepository.save(project);
        projectRepository.deleteById(project.getId());
        projectRepository.flush();
    }

    @Override
    public Optional<Project> update(Long projectId, String name, String description, String repoUrl, int year, List<String> courseIds, List<String> mentorIds, List<String> teamMemberIds, String createdByStudentId) {
        List<Subject> subjects = new ArrayList<>();
        List<Professor> mentors = new ArrayList<>();
        List<Student> students = new ArrayList<>();

        for (String courseId : courseIds) {
            subjects.add(subjectRepository.findById(courseId).orElseThrow(RuntimeException::new));
        }
        for (String mentorId : mentorIds) {
            mentors.add(professorRepository.findById(mentorId).orElseThrow(RuntimeException::new));
        }
        for (String teamMemberId : teamMemberIds) {
            students.add(studentRepository.findById(teamMemberId).orElseThrow(RuntimeException::new));
        }

        return projectRepository
                .findById(projectId)
                .map(existingProject -> {
                    existingProject.setName(name);
                    existingProject.setShortDescription(description);
                    existingProject.setRepoUrl(repoUrl);
                    existingProject.setYear(year);
                    existingProject.setMentors(mentors);
                    existingProject.setSubjects(subjects);
                    existingProject.setTeamMembers(students);
                    return projectRepository.save(existingProject);
                });
    }
}
