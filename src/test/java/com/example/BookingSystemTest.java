package com.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    RoomRepository roomRepository;

    @Mock
    NotificationService notificationService;

    @Mock
    TimeProvider timeProvider;

    @Mock
    Room room;

    @Mock
    Booking booking;

    @InjectMocks
    BookingSystem bookingSystem;

    void time() {
        when(timeProvider.getCurrentTime()).thenReturn(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0));
    }

    @Test
    @DisplayName("RoomId null throws exception")
    void roomIdNullThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom(null,
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }

    @Test
    @DisplayName("StartTime null throws exception")
    void startTimeNullThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("T1",
                null,
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }

    @Test
    @DisplayName("EndTime null throws exception")
    void endTimeNullThrowsException() {
        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("T1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                null));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }

    @Test
    @DisplayName("Booking in past time throws exception")
    void bookingInPastTimeThrowsException() {
        time();
        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 11, 59),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)));
        assertEquals("Kan inte boka tid i dåtid", exception.getMessage());
    }

    @Test
    @DisplayName("booking with endTime before startTime throws exception")
    void bookingWithEndTimeBeforeStartTimeThrowsException() {
        time();
        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 4)));
        assertEquals("Sluttid måste vara efter starttid", exception.getMessage());
    }

    @Test
    @DisplayName("booking room with invalid id throws exception")
    void bookingRoomWithInvalidIdThrowsException() {
        time();
        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)));
        assertEquals("Rummet existerar inte", exception.getMessage());
    }

    @Test
    @DisplayName("Book room return false if room is not available")
    void bookRoomReturnFalseIfRoomIsNotAvailable() {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)))
                .thenReturn(false);

        assertThat(bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5))).isFalse();

    }
    @Test
    @DisplayName("bookRoom returns true if room is available")
    void bookRoomReturnTrueIfRoomIsAvailable() {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)))
                .thenReturn(true);
        boolean result = bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5));
        assertThat(result).isTrue();
    }
    @Test
    @DisplayName("Verify that booking has been made")
    void verifyThatBookingHasBeenMade(){
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)))
                .thenReturn(true);
        bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5));
        verify(room).addBooking(any(Booking.class));
    }



    @Test
    void getAvailableRooms() {
    }

    @Test
    void cancelBooking() {
    }
}
