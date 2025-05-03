package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.Professor;
import mk.ukim.finki.wp.repoagregator.model.Project;

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
}
