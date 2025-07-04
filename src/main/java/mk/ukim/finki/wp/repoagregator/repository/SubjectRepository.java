package mk.ukim.finki.wp.repoagregator.repository;

import mk.ukim.finki.wp.repoagregator.model.Subject;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface SubjectRepository extends JpaSpecificationRepository<Subject,String>{
}
