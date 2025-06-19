package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import mk.ukim.finki.wp.repoagregator.model.Project;
import mk.ukim.finki.wp.repoagregator.model.Student;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(
            String name,
            String description,
            String repoUrl,
            int year,
            List<String> courseIds,
            List<String> mentorIds,
            List<String> teamMemberIds,
            String createdByStudentId
    );
    Project findById(Long id);
    List<Project> findAllByStudent(Student student);
    List<Project> findAllByMentor(String mentorId);
    Project update(Long projectId, ProjectStatus projectStatus, ApprovalComment approvalComment);
    Page<Project> findPage(String search, String course, Integer year, Integer pageNum, Integer pageSize);
    void deleteProject(Long projectId);
    Optional<Project> update(
            Long projectId,
            String name,
            String description,
            String repoUrl,
            int year,
            List<String> courseIds,
            List<String> mentorIds,
            List<String> teamMemberIds,
            String createdByStudentId
    );
}
