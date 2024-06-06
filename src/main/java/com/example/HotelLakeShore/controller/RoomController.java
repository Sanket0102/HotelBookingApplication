package com.example.HotelLakeShore.controller;

import com.example.HotelLakeShore.exception.PhotoRetrieverException;
import com.example.HotelLakeShore.exception.ResourceNotFoundException;
import com.example.HotelLakeShore.model.BookedRoom;
import com.example.HotelLakeShore.model.Room;
import com.example.HotelLakeShore.response.BookingResponse;
import com.example.HotelLakeShore.response.RoomResponse;
import com.example.HotelLakeShore.service.BookedRoomService;
import com.example.HotelLakeShore.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.awt.print.Book;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final BookedRoomService bookedRoomService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo, @RequestParam("roomType") String roomType,@RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
         Room savedRoom = roomService.addNewRoom(photo ,roomType,roomPrice);
         RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(),savedRoom.getRoomPrice());
         return ResponseEntity.ok(response);
    }

    @GetMapping("/room/room-types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException{
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for(Room room: rooms){
            byte[] photoByte = roomService.getRoomPhotoByRoomId(room.getId());
            String base64 = Base64.encodeBase64String(photoByte);
            RoomResponse roomResponse = getRoomResponse(room);
            roomResponse.setPhoto(base64);
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long roomId){
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable("roomId") Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice ,
                                                   @RequestParam(required = false)MultipartFile photo) throws SQLException,IOException{
        //Client -> Server
        byte[] photoBytes = photo != null && !photo.isEmpty() ? photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
        Room theRoom = roomService.updateRoom(roomId, roomType,roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);

        //Server -> Client
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") Long roomId){
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room ->  {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not Found"));
    }

    @GetMapping("/avialable-rooms")
    public ResponseEntity<List<RoomResponse>> getAvialableRooms(@RequestParam("checkInDate")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                                @RequestParam("checkOutDate")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                                                @RequestParam("roomType") String roomType) throws SQLException {
        List <Room> avialableRooms = roomService.getAvialableRooms(checkInDate,checkOutDate,roomType);
        List <RoomResponse> avialableRoomResponses = new ArrayList<>();
        for(Room room : avialableRooms){
            byte [] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes != null && photoBytes.length > 0){
                String photoBase64 = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                avialableRoomResponses.add(roomResponse);
            }
        }
        if(avialableRoomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.ok(avialableRoomResponses);
        }
    }

    private RoomResponse getRoomResponse(Room room){
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        /*List<BookingResponse> bookingInfo = bookings
                .stream().
                map(booking -> new BookingResponse(booking.getBookingId(),
                                                   booking.getCheckInDate(),
                                                   booking.getCheckOutDate(),
                                                   booking.getBookingConformationCode())).toList();*/
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1,(int)photoBlob.length());
            }
            catch(SQLException e){
                throw new PhotoRetrieverException("Error Retrieving photo");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(),room.isBooked(),photoBytes);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long id){
        return bookedRoomService.getAllBookingsByRoomId(id);
    }
}
