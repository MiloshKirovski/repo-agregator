package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.Room;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RoomType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaSpecificationRepository<Room, String> {

}
