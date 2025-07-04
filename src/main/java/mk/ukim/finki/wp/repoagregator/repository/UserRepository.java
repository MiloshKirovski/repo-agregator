package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaSpecificationRepository<User, String> {
    Optional<User> findByid(String id);
}
