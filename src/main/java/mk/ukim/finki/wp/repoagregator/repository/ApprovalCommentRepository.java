package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalCommentRepository extends JpaSpecificationRepository<ApprovalComment,Long> {
}
