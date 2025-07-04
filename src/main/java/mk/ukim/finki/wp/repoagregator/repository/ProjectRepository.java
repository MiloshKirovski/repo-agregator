package mk.ukim.finki.wp.repoagregator.repository;

import jdk.jshell.Snippet;
import mk.ukim.finki.wp.repoagregator.model.Professor;
import mk.ukim.finki.wp.repoagregator.model.Project;
import mk.ukim.finki.wp.repoagregator.model.Student;

import lombok.*;
import mk.ukim.finki.wp.repoagregator.model.Subject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaSpecificationRepository<Project,Long> {
    List<Project> findAllByCreatedBy(Student createdBy);
    List<Project> findAllByMentorsContaining(Professor mentorId);
    List<Project> findAllBySubjectsContaining(Subject subject);

}
