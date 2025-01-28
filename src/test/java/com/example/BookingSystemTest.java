package com.example;

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
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {


    @Mock
    RoomRepository roomRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    TimeProvider timeProvider;


    @InjectMocks
    BookingSystem bookingSystem;
//    @Test
//    @DisplayName("RoomId null throws exception2")
//    void roomIdNullThrowsException2() {
////        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2025, Month.JANUARY,27,12, 0));
////        when(roomRepository.findById("R1")).thenReturn(Optional.of(new Room("R1", "penthouse")));
//
//        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
//                null,
//                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)));
//        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
//    }


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
        when(timeProvider.getCurrentTime()).thenReturn(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0));


        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 11, 59),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)));
        assertEquals("Kan inte boka tid i dåtid", exception.getMessage());
    }

    @Test
    @DisplayName("booking with endTime before startTime throws exception")
    void bookingWithEndTimeBeforeStartTimeThrowsException() {
        when(timeProvider.getCurrentTime()).thenReturn(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0));

        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5),
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 4)));
        assertEquals("Sluttid måste vara efter starttid", exception.getMessage());
    }
    @Test
    @DisplayName("booking room with invalid id throws exception")
    void bookingRoomWithInvalidIdThrowsException(){
        when(timeProvider.getCurrentTime()).thenReturn(
                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0));

        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY,27,12, 0),
                LocalDateTime.of(2025, Month.JANUARY,27,12, 5)));
        assertEquals("Rummet existerar inte", exception.getMessage());
    }

//    @Test
//    @DisplayName("Book room return false if room is not available")
//    void bookRoomReturnFalseIfRoomIsNotAvailable() {
//        when(timeProvider.getCurrentTime()).thenReturn(
//                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0));
//
//        assertThat(bookingSystem.bookRoom("R1",
//                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
//                LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5))).isFalse();
//
//    }


    @Test
    void getAvailableRooms() {
    }

    @Test
    void cancelBooking() {
    }
}
