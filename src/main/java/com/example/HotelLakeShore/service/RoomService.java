package com.example.HotelLakeShore.service;

import com.example.HotelLakeShore.exception.InternalServerException;
import com.example.HotelLakeShore.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
   public Room addNewRoom(MultipartFile photo , String roomType, BigDecimal roomPrice) throws SQLException, IOException;
   public List<String> getAllRoomTypes();

   public List<Room> getAllRooms();

   public byte[] getRoomPhotoByRoomId(Long id) throws SQLException;

   public void deleteRoom(Long roomId);

   public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) throws InternalServerException;

   public Optional<Room> getRoomById(Long roomId);

   public List<Room> getAvialableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
