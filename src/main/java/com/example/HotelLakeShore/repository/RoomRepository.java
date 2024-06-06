package com.example.HotelLakeShore.repository;

import com.example.HotelLakeShore.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query("SELECT r FROM Room r "+
     " WHERE r.roomType LIKE %:roomType%" +
     " AND r.id NOT IN ( "+
     "  SELECT br.room.id FROM BookedRoom br "+
     "  WHERE((br.checkInDate <= :checkOutDate) AND (br.checkOutDate >= :checkInDate))"+
    ")")
    List <Room> findAvialableRoomsByDatesAndTypes(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
