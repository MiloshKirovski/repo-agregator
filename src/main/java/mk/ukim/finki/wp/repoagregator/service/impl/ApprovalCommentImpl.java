package mk.ukim.finki.wp.repoagregator.service.impl;

import mk.ukim.finki.wp.repoagregator.model.ApprovalComment;
import mk.ukim.finki.wp.repoagregator.model.Project;
import mk.ukim.finki.wp.repoagregator.repository.ApprovalCommentRepository;
import mk.ukim.finki.wp.repoagregator.service.ApprovalCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalCommentImpl implements ApprovalCommentService {
    private final ApprovalCommentRepository approvalCommentRepository;

    public ApprovalCommentImpl(ApprovalCommentRepository approvalCommentRepository) {
        this.approvalCommentRepository = approvalCommentRepository;
    }

    @Override
    public ApprovalComment save(ApprovalComment approvalComment) {
        return approvalCommentRepository.save(approvalComment);
    }

    @Override
    public ApprovalComment findByProject(Project project) {
        return approvalCommentRepository.findByProject(project);
    }
}
