package com.example.HotelLakeShore.service;

import com.example.HotelLakeShore.exception.InvalidBookingRequestException;
import com.example.HotelLakeShore.exception.ResourceNotFoundException;
import com.example.HotelLakeShore.model.BookedRoom;
import com.example.HotelLakeShore.model.Room;
import com.example.HotelLakeShore.repository.BookedRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookedRoomServiceImpl implements BookedRoomService {

    @Autowired
    private BookedRoomRepository bookedRoomRepository;

    @Autowired
    private RoomService roomService;

    @Override
    public List<BookedRoom> getAllBookings() {

        return bookedRoomRepository.findAll();
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId){
        return bookedRoomRepository.findByRoomId(roomId);
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode){
          return bookedRoomRepository.findByBookingConfirmationCode(confirmationCode)
                  .orElseThrow(() -> new ResourceNotFoundException("No Booking Found with Confirmation Code :" + confirmationCode));
    }

    @Override
    public List<BookedRoom> findBookingByUserId(String userId){
        return bookedRoomRepository.findByGuestEmail(userId);

    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest){
        System.out.println("save Booking()");
        System.out.println("Num of Adults" + bookingRequest.getNumOfAdults());
        System.out.println("Num of Children" + bookingRequest.getNumOfChildren());
        System.out.println("Total "+ bookingRequest.getTotalNumOfGuest());
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check in Date must be come before check out");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvialable = roomIsAvialable(bookingRequest, existingBookings);
        if(roomIsAvialable){
            room.addBooking(bookingRequest);
            bookedRoomRepository.save(bookingRequest);
        }
        else{
            throw new InvalidBookingRequestException("Sorry..! This room is not avialable for selected days");
        }
        System.out.println(bookingRequest.getBookingConfirmationCode());
        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public void cancelBooking(Long roomId){
        bookedRoomRepository.deleteById(roomId);
    }

    private boolean roomIsAvialable(BookedRoom bookingRequest, List<BookedRoom> existingBookings){
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                    bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                            ||
                    bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                            ||
(bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckOutDate()) && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                            ||
(bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                            ||
(bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                                            ||
(bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))
                                            ||
(bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}
