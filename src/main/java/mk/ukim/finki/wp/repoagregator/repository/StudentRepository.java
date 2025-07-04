package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.Student;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository

public interface StudentRepository extends JpaSpecificationRepository<Student, String> {
    Optional<Student> findByEmail(String email);
}
