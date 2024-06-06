package com.example.HotelLakeShore.controller;

import com.example.HotelLakeShore.exception.InvalidBookingRequestException;
import com.example.HotelLakeShore.exception.ResourceNotFoundException;
import com.example.HotelLakeShore.model.BookedRoom;
import com.example.HotelLakeShore.model.Room;
import com.example.HotelLakeShore.response.BookingResponse;
import com.example.HotelLakeShore.response.RoomResponse;
import com.example.HotelLakeShore.service.BookedRoomService;
import com.example.HotelLakeShore.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/bookings")
public class BookingRoomController {

    @Autowired
    private BookedRoomService bookedRoomService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookedRoom> bookings = bookedRoomService.getAllBookings();
        List <BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoom booking : bookings){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }


    @GetMapping("/user/{userId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable("userId") String userId){

            List<BookedRoom> bookings = bookedRoomService.findBookingByUserId(userId);
            List<BookingResponse> bookingResponses = new ArrayList<>();
            for(BookedRoom booking: bookings ){
                BookingResponse bookingResponse = getBookingResponse(booking);
                bookingResponses.add(bookingResponse);
            }
            return ResponseEntity.ok(bookingResponses);


    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable("confirmationCode") String confirmationCode){
        try{
            BookedRoom booking = bookedRoomService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }
        catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable("roomId") Long roomId,
                                          @RequestBody BookedRoom bookingRequest){
        System.out.println("BookedRoom Controller");
        System.out.println("Num of Adults" + bookingRequest.getNumOfAdults());
        System.out.println("Num of Children" + bookingRequest.getNumOfChildren());
        System.out.println("Total "+ bookingRequest.getTotalNumOfGuest());
        try{
            String confirmationCode = bookedRoomService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room Booked SuccessFully..! Your booking confirmation code is :" + confirmationCode);

        }
        catch(InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable("bookingId") Long bookingId){
        bookedRoomService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking){
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(theRoom.getId(), theRoom.getRoomType(), theRoom.getRoomPrice());
        return new BookingResponse(booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getGuestFullName(), booking.getGuestEmail(),booking.getNumOfAdults(),
                booking.getNumOfChildren(), booking.getTotalNumOfGuest(), booking.getBookingConfirmationCode(), room);
    }

}
