package mk.ukim.finki.wp.repoagregator.repository;

import mk.ukim.finki.wp.repoagregator.model.Project;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaSpecificationRepository<Project,Long> {
}
