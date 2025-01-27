package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
class BookingSystemTest {

    @Mock
    RoomRepositoryTestDouble roomRepository;

    @Mock
    NotificationService notificationService;

    @Test
    @DisplayName("RoomId null throws exception")
    void roomIdNullThrowsException(){
        TimeProviderForTest timeProvider = new TimeProviderForTest();

        BookingSystem bookingSystem = new BookingSystem(timeProvider,roomRepository,notificationService );

        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom(null,
                LocalDateTime.of(2025, Month.JANUARY,27,12, 0),
                LocalDateTime.of(2025, Month.JANUARY,27,12, 5)));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }
    @Test
    @DisplayName("StartTime null throws exception")
    void startTimeNullThrowsException(){
        TimeProviderForTest timeProvider = new TimeProviderForTest();
        BookingSystem bookingSystem = new BookingSystem(timeProvider,roomRepository,notificationService );

        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("T1",
                null,
                LocalDateTime.of(2025, Month.JANUARY,27,12, 5)));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }
    @Test
    @DisplayName("EndTime null throws exception")
    void endTimeNullThrowsException(){
        TimeProviderForTest timeProvider = new TimeProviderForTest();
        BookingSystem bookingSystem = new BookingSystem(timeProvider,roomRepository,notificationService );

        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("T1",
                LocalDateTime.of(2025, Month.JANUARY,27,12, 0),
                null));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }
    @Test
    @DisplayName("booking in past time throws exception")
    void bookingInPastTimeThrowsException(){
        TimeProviderForTest timeProvider = new TimeProviderForTest();

        BookingSystem bookingSystem = new BookingSystem(timeProvider,roomRepository,notificationService );

        var exception = assertThrows(IllegalArgumentException.class, () -> bookingSystem.bookRoom("R1",
                LocalDateTime.of(2025, Month.JANUARY,27,11, 59),
                LocalDateTime.of(2025, Month.JANUARY,27,12, 5)));
        assertEquals("Kan inte boka tid i dåtid", exception.getMessage());
    }


    @Test
    void getAvailableRooms() {
    }

    @Test
    void cancelBooking() {
    }
}
