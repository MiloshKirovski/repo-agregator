package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.Professor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaSpecificationRepository<Professor, String> {
    Optional<Professor> findByid(String mentorId);
}
