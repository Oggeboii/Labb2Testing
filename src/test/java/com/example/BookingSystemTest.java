package com.example;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    static LocalDateTime now = java.time.LocalDateTime.of(2025, Month.JANUARY, 27, 12, 0);
    static LocalDateTime nowPlusFive = java.time.LocalDateTime.of(2025, Month.JANUARY, 27, 12, 5);

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

    static Stream<Arguments> nullBookingParameters() {
        return Stream.of(
                Arguments.of(null, now, nowPlusFive),
                Arguments.of("R1", null, nowPlusFive),
                Arguments.of("R1", now, null));
    }
    static Stream<Arguments> InvalidBookingParameters() {
        return Stream.of(
                Arguments.of("R1", now.minusMinutes(1), nowPlusFive, "Kan inte boka tid i dåtid"),
                Arguments.of("R1", nowPlusFive, nowPlusFive.minusMinutes(1), "Sluttid måste vara efter starttid"),
                Arguments.of("R1", now, nowPlusFive, "Rummet existerar inte"));
    }

    void time() {
        when(timeProvider.getCurrentTime()).thenReturn(now);
    }

    @ParameterizedTest
    @MethodSource("nullBookingParameters")
    @DisplayName("booking with null for RoomId, startTime or endTime throws exception")
    void bookingWithNullForRoomIdStartTimeOrEndTimeThrowsException(String roomId, LocalDateTime startTime, LocalDateTime endTime) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom(roomId, startTime, endTime));
        assertEquals("Bokning kräver giltiga start- och sluttider samt rum-id", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("InvalidBookingParameters")
    @DisplayName("Booking in past time, endTime before startTime or non-existing room throws exception ")
    void bookingInPastTimeEndTimeBeforeStartTimeOrNonExistingRoomThrowsException(String roomId, LocalDateTime startTime, LocalDateTime endTime, String exceptionMessage) {
        time();
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.bookRoom(roomId, startTime, endTime));

        assertEquals(exceptionMessage, exception.getMessage());
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

    @Nested
    @DisplayName("Tests for successful booking")
    class successfulBooking {
        @BeforeEach
        void setUp() {
            time();
            when(roomRepository.findById("R1")).thenReturn(Optional.of(room));
            when(room.isAvailable(now, nowPlusFive)).thenReturn(true);
        }

        @Test
        @DisplayName("bookRoom returns true if room is available")
        void bookRoomReturnTrueIfRoomIsAvailable() {

            boolean result = bookingSystem.bookRoom("R1", now, nowPlusFive);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Verify that booking has been made")
        void verifyThatBookingHasBeenMade() {

            bookingSystem.bookRoom("R1", now, nowPlusFive);

            verify(room).addBooking(any(Booking.class));
        }

        @Test
        @DisplayName("Verify that room has been saved")
        void verifyThatRoomHasBeenSaved() {

            bookingSystem.bookRoom("R1", now, nowPlusFive);

            verify(roomRepository).save(room);
        }

        @Test
        @DisplayName("Verify that booking confirmation has been sent")
        void verifyThatBookingConfirmationHasBeenSent() throws NotificationException {

            doThrow(new NotificationException("Notifiering misslyckades"))
                    .when(notificationService).sendBookingConfirmation(any(Booking.class));

            bookingSystem.bookRoom("R1", now, nowPlusFive);
            verify(notificationService).sendBookingConfirmation(any(Booking.class));
        }
    }

    static Stream<Arguments> invalidAvailableRoomsParameters() {
        return Stream.of(
                Arguments.of(null, nowPlusFive, "Måste ange både start- och sluttid"),
                Arguments.of( nowPlusFive, null, "Måste ange både start- och sluttid"),
                Arguments.of( nowPlusFive, now, "Sluttid måste vara efter starttid"));
    }

    @ParameterizedTest
    @MethodSource("invalidAvailableRoomsParameters")
    @DisplayName("AvailableRooms throws exception if startTime or endTime is null or endTime before startTime")
    void availableRoomsThrowsExceptionIfStartTimeOrEndTimeIsNullOrEndTimeBeforeStartTime(LocalDateTime startTime, LocalDateTime endTime, String exceptionMessage) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                bookingSystem.getAvailableRooms(startTime, endTime));

        assertEquals(exceptionMessage, exception.getMessage());
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
    @Test
    @DisplayName("Verify that booking has been removed from room ")
    void verifyThatBookingHasBeenRemovedFromRoom(){
        time();
        when(room.hasBooking("BookingId")).thenReturn(true);
        when(room.getBooking("BookingId")).thenReturn(booking);
        when(booking.getStartTime()).thenReturn(nowPlusFive);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        bookingSystem.cancelBooking("BookingId");
        verify(room).removeBooking("BookingId");
    }
    @Test
    @DisplayName("Verify that cancellation confirmation has been sent")
    void verifyThatCancellationConfirmationHasBeenSent() throws NotificationException {
        time();
        when(room.hasBooking("BookingId")).thenReturn(true);
        when(room.getBooking("BookingId")).thenReturn(booking);
        when(booking.getStartTime()).thenReturn(nowPlusFive);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        doThrow(new NotificationException("Notifiering misslyckades"))
                .when(notificationService).sendCancellationConfirmation(any(Booking.class));
        bookingSystem.cancelBooking("BookingId");
        verify(notificationService).sendCancellationConfirmation(any(Booking.class));
    }
    @Test
    @DisplayName("Cancel booking returns true")
    void cancelBookingReturnsTrue(){
        time();
        when(room.hasBooking("BookingId")).thenReturn(true);
        when(room.getBooking("BookingId")).thenReturn(booking);
        when(booking.getStartTime()).thenReturn(nowPlusFive);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        boolean result = bookingSystem.cancelBooking("BookingId");
        assertThat(result).isTrue();

    }


}
