package com.example.HotelLakeShore.service;

import com.example.HotelLakeShore.exception.InternalServerException;
import com.example.HotelLakeShore.model.Room;
import com.example.HotelLakeShore.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import com.example.HotelLakeShore.exception.ResourceNotFoundException;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.getBytes;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService{
    @Autowired
    private RoomRepository roomRepository;
    @Override
    public Room addNewRoom(MultipartFile photo , String roomType, BigDecimal roomPrice) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!photo.isEmpty()){
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob((photoBytes));
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);

    }

    @Override
    public List<String> getAllRoomTypes() {
        System.out.println(roomRepository.findDistinctRoomTypes());
        return roomRepository.findDistinctRoomTypes();

    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long id) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(id);
        if(theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry...! Room not Found");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1,(int)photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId){
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if(theRoom.isPresent()) {
            roomRepository.deleteById(roomId);
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) throws InternalServerException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found Exception"));
        if(roomType != null){
            room.setRoomType(roomType);
        }
        if(roomPrice != null){
            room.setRoomPrice(roomPrice);
        }
        if(photoBytes != null && photoBytes.length > 0){
            try {
                room.setPhoto(new SerialBlob(photoBytes));
            }
            catch(SQLException e){
                throw new InternalServerException("Error updating room");
            }
        }
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId){
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvialableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvialableRoomsByDatesAndTypes(checkInDate,checkOutDate,roomType);
    }

}
