package mk.ukim.finki.wp.repoagregator.repository;

import mk.ukim.finki.wp.repoagregator.model.Subject;
import org.springframework.stereotype.Service;

@Service
public interface SubjectRepository extends JpaSpecificationRepository<Subject,String>{
}
