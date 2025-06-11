package mk.ukim.finki.wp.repoagregator.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mk.ukim.finki.wp.repoagregator.model.enumerations.ProjectStatus;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RepositoryType;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "project_repo")
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String shortDescription;

    private String repoUrl;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    private RepositoryType platform;

    @OneToOne
    private ApprovalComment approvalComment;

    private int year;

    @ManyToMany
    private List<Student> teamMembers;

    @ManyToMany
    private List<Professor> mentors;

    @ManyToMany
    private List<Subject> subjects;

    @ManyToOne
    private Student createdBy;

    public Project(String name, String shortDescription, String repoUrl, ProjectStatus projectStatus, RepositoryType platform, ApprovalComment approvalComment, int year, List<Student> teamMembers, List<Professor> mentors, List<Subject> subjects, Student createdBy) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.repoUrl = repoUrl;
        this.projectStatus = projectStatus;
        this.platform = platform;
        this.approvalComment = approvalComment;
        this.year = year;
        this.teamMembers = teamMembers;
        this.mentors = mentors;
        this.subjects = subjects;
        this.createdBy = createdBy;
    }
}