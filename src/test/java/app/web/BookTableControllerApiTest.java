package app.web;

import app.APIReservation.DTO.ReservationRequest;
import app.APIReservation.ReservationClient;
import app.APIReservation.ReservationService;
import app.Security.UserInfo;
import app.User.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookTableController.class)
class BookTableControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationClient reservationClient;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @WithMockUser(roles = "CLIENT")
    void bookTablePage_ShouldReturnModelWithReservationRequest() throws Exception {

        UUID userId = UUID.randomUUID();
        UserInfo userInfo = new UserInfo(userId, "user", "password", UserRole.CLIENT, true);
        ReservationRequest mockRequest = new ReservationRequest();

        when(reservationService.addUserInfo(any(UUID.class))).thenReturn(mockRequest);

        mockMvc.perform(get("/reservation")
                        .with(SecurityMockMvcRequestPostProcessors.user(userInfo)))
                .andExpect(status().isOk())
                .andExpect(view().name("book-table"))
                .andExpect(model().attributeExists("reservationRequest"));

        verify(reservationService).addUserInfo(userId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void makeReservation_ShouldRedirectToHome() throws Exception {

        ReservationRequest request = new ReservationRequest();
        request.setDate(LocalDate.now());
        request.setGuests(4);
        request.setMessage("Window seat");

        mockMvc.perform(post("/reservation")
                        .param("dateTime", request.getDate().toString())
                        .param("numberOfPeople", String.valueOf(request.getGuests()))
                        .param("additionalNotes", request.getMessage())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(reservationClient).makeReservation(any(ReservationRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void bookTablePage_ShouldDenyAccessForNonClient() throws Exception {
        mockMvc.perform(get("/reservation"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void makeReservation_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/reservation"))
                .andExpect(status().isForbidden());
    }
}

