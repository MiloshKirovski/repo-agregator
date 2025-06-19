package mk.ukim.finki.wp.repoagregator.repository;


import mk.ukim.finki.wp.repoagregator.model.Room;
import mk.ukim.finki.wp.repoagregator.model.enumerations.RoomType;

import java.util.List;
import java.util.Optional;


public interface RoomRepository extends JpaSpecificationRepository<Room, String> {

}
