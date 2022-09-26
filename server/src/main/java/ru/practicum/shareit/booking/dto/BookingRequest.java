package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
