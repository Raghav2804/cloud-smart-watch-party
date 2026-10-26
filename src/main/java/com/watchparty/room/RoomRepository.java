package com.watchparty.room;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @EntityGraph(attributePaths = "members")
    Optional<Room> findByCode(String code);

    @EntityGraph(attributePaths = "members")
    List<Room> findByMembers_Username(String username);
}
