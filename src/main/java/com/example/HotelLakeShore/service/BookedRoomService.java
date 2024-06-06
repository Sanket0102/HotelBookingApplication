package com.example.HotelLakeShore.service;

import com.example.HotelLakeShore.model.BookedRoom;
import com.example.HotelLakeShore.model.Room;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BookedRoomService {
    public List<BookedRoom> getAllBookings();

    public BookedRoom findByBookingConfirmationCode(String confirmationCode);

    public String saveBooking(Long roomId, BookedRoom bookingRequest);

    public void cancelBooking(Long roomId);

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    public List<BookedRoom> findBookingByUserId(String userId);


}
