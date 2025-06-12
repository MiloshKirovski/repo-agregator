package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import mk.ukim.finki.wp.repoagregator.model.Project;

import java.util.List;

public interface ApprovalCommentService {
    ApprovalComment save(ApprovalComment approvalComment);

   ApprovalComment findByProject(Project project);
}
