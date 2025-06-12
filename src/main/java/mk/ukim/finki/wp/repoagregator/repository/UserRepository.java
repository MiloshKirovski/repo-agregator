package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.User;

import java.util.Optional;

public interface UserRepository extends JpaSpecificationRepository<User, String> {
    Optional<User> findByid(String id);
}
