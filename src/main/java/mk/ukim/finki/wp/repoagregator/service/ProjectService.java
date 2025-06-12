package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import mk.ukim.finki.wp.repoagregator.model.Professor;
import mk.ukim.finki.wp.repoagregator.model.Project;
import mk.ukim.finki.wp.repoagregator.model.Student;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;

import java.util.List;

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

    void approveProject(Long projectId, String professorId, String comment);

    void rejectProject(Long projectId, String professorId, String comment);
    List<Project> findAll();
    Project findById(Long id);
    List<Project> findAllApproved();
    List<Project> findAllByStudent(Student student);
    List<Project> findAllByMentor(String mentorId);
    Project update(Long projectId, ProjectStatus projectStatus, ApprovalComment approvalComment);
}
