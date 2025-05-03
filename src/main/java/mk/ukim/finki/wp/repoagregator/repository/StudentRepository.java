package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.Student;

import java.util.Optional;


public interface StudentRepository extends JpaSpecificationRepository<Student, String> {

    Optional<Student> findByEmail(String email);
}
