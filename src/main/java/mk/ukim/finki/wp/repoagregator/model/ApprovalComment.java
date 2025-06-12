package mk.ukim.finki.wp.repoagregator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
public class ApprovalComment {
    @Id
    @GeneratedValue
    private Long id;

    private String comment;
    private boolean approved;

    @OneToOne(mappedBy = "approvalComment")
    private Project project;

    @ManyToOne
    private Professor reviewer;

    public ApprovalComment() {}

    public ApprovalComment(String comment, boolean approved, Project project, Professor reviewer) {
        this.comment = comment;
        this.approved = approved;
        this.project = project;
        this.reviewer = reviewer;
    }
}