package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.Professor;

import java.util.Optional;

public interface ProfessorRepository extends JpaSpecificationRepository<Professor, String> {
    Optional<Professor> findByid(String mentorId);
}
