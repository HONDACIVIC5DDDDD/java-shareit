package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.model.Status.WAITING;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDtoBooking;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDtoBooking;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingServiceImpl bookingServiceImpl;
    @Autowired
    private MockMvc mockMvc;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        booking = new Booking(1L, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1), new Item(), new User(), WAITING);

    }

    @Test
    void bookingRequest() throws Exception {
        when(bookingServiceImpl.bookingRequest(any(), anyLong(), anyLong()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

    }


    @Test
    void confirmOrRejectBooking() throws Exception {
        when(bookingServiceImpl.confirmOrRejectBooking(any(), any(), anyLong()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/1")
                        .content(objectMapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));

    }

    @Test
    void getBookingByOwner() throws Exception {
        BookingDto bookingDto = toBookingDto(booking);
        when(bookingServiceImpl.getBookingByOwner(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));

    }

    @Test
    void getBookingByOwnerAll() throws Exception {
        List<BookingDto> bookingDto = List.of(toBookingDto(booking));
        when(bookingServiceImpl.getBookingByOwnerAll(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/owner")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }


    private BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(toUserDtoBooking(booking.getBooker()));
        bookingDto.setItem(toItemDtoBooking(booking.getItem()));
        return bookingDto;
    }
}