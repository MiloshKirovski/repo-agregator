package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import mk.ukim.finki.wp.repoagregator.model.Project;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalCommentRepository extends JpaSpecificationRepository<ApprovalComment,Long> {
    ApprovalComment findByProject(Project project);
}
