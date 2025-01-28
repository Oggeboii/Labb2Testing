package com.example;

import net.bytebuddy.asm.MemberSubstitution;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    LocalDateTime now = LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0);
    LocalDateTime nowPlusFive = LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5);

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

    static Stream<Arguments> roomTimeNull() {
        return Stream.of(
                Arguments.of(null,
                        LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                        LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)),
                Arguments.of("R1",
                        null,
                        LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5)),
                Arguments.of("R1",
                        LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0),
                        null));
    }

    void time() {
        when(timeProvider.getCurrentTime()).thenReturn(now);
    }

    @ParameterizedTest
    @MethodSource("roomTimeNull")
    void nullForRoomIdStartTimeEndTimeThrowsException(String a1, LocalDateTime a2, LocalDateTime a3) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom(a1, a2, a3));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }

    @Test
    @DisplayName("Booking in past time throws exception")
    void bookingInPastTimeThrowsException() {
        time();
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom("R1",
                        LocalDateTime.of(2025, Month.JANUARY, 27, 11, 59),
                        nowPlusFive));

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
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom("R1", now, nowPlusFive));

        assertEquals("Rummet existerar inte", exception.getMessage());
    }

    @Test
    @DisplayName("Book room return false if room is not available")
    void bookRoomReturnFalseIfRoomIsNotAvailable() {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(now, nowPlusFive))
                .thenReturn(false);

        assertThat(bookingSystem.bookRoom("R1", now, nowPlusFive))
                .isFalse();

    }

    @Test
    @DisplayName("bookRoom returns true if room is available")
    void bookRoomReturnTrueIfRoomIsAvailable() {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(now, nowPlusFive))
                .thenReturn(true);
        boolean result = bookingSystem.bookRoom("R1", now, nowPlusFive);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Verify that booking has been made")
    void verifyThatBookingHasBeenMade() {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(now, nowPlusFive))
                .thenReturn(true);
        bookingSystem.bookRoom("R1", now, nowPlusFive);

        verify(room).addBooking(any(Booking.class));
    }

    @Test
    @DisplayName("Verify that room has been saved")
    void verifyThatRoomHasBeenSaved() {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(now, nowPlusFive))
                .thenReturn(true);
        bookingSystem.bookRoom("R1", now, nowPlusFive);

        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("Verify that booking confirmation has been sent")
    void verifyThatBookingConfirmationHasBeenSent() throws NotificationException {
        time();
        when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
        when(room.isAvailable(now, nowPlusFive))
                .thenReturn(true);
        doThrow(new NotificationException("Notifiering misslyckades"))
                .when(notificationService).sendBookingConfirmation(any(Booking.class));

        bookingSystem.bookRoom("R1", now, nowPlusFive);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    @DisplayName("availableRooms throw exception if startTime is null")
    void availableRoomsThrowExceptionIfStartTimeIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(null, nowPlusFive));

        assertEquals("Måste ange både start- och sluttid", exception.getMessage());
    }

    @Test
    @DisplayName("availableRooms throw exception if endTime is null")
    void availableRoomsThrowExceptionIfEndTimeIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(now, null));

        assertEquals("Måste ange både start- och sluttid", exception.getMessage());
    }

    @Test
    @DisplayName("availableRooms throw exception if endTime is before startTime")
    void availableRoomsThrowExceptionIfEndTimeIsBeforeStartTime() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(nowPlusFive, now));

        assertEquals("Sluttid måste vara efter starttid", exception.getMessage());
    }

    @Test
    @DisplayName("availableRooms returns list of available rooms")
    void availableRoomsReturnsListOfAvailableRooms() {

        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(room.isAvailable(now, nowPlusFive)).thenReturn(true);
        bookingSystem.getAvailableRooms(now, nowPlusFive);

        assertThat(bookingSystem.getAvailableRooms(now, nowPlusFive)).isEqualTo(List.of(room));
    }

    @Test
    @DisplayName("CancelBooking throws exception when bookingId is null")
    void cancelBookingThrowsExceptionWhenBookingIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.cancelBooking(null));
        assertEquals("Boknings-id kan inte vara null", exception.getMessage());
    }

    @Test
    @DisplayName("CancelBooking returns false if roomWithBooking returns empty")
    void cancelBookingReturnsFalseIfRoomWithBookingReturnsEmpty() {

        bookingSystem.cancelBooking("BookingId");
        assertThat(bookingSystem.cancelBooking("BookingId")).isEqualTo(false);
    }

    @Test
    @DisplayName("Canceling started booking throws exception ")
    void cancelingStartedBookingThrowsException() {
        time();
        when(room.hasBooking("BookingId")).thenReturn(true);
        when(room.getBooking("BookingId")).thenReturn(booking);
        when(booking.getStartTime()).thenReturn(now.minusMinutes(1));

        when(roomRepository.findAll()).thenReturn(List.of(room));
        var exception = assertThrows(IllegalStateException.class, () ->
                bookingSystem.cancelBooking("BookingId"));
        assertEquals("Kan inte avboka påbörjad eller avslutad bokning",
                exception.getMessage());
    }


}
