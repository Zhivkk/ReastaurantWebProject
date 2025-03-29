package app.UnitTests;

import app.APIReservation.DTO.ReservationRequest;
import app.APIReservation.ReservationService;
import app.User.model.User;
import app.User.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void addUserInfo_WithValidId_ReturnsReservationRequestWithUserData() {
        // Given
        UUID userId = UUID.randomUUID();
        User mockUser = User.builder()
                .phone("0888123456")
                .email("test@example.com")
                .username("testUser")
                .build();

        when(userService.getById(userId)).thenReturn(mockUser);

        // When
        ReservationRequest result = reservationService.addUserInfo(userId);

        // Then
        verify(userService, times(1)).getById(userId); // Проверка дали userService.getById() се вика точно веднъж
        assertNotNull(result);
        assertEquals("0888123456", result.getUserPhone());
        assertEquals("test@example.com", result.getUserEmail());
        assertEquals("testUser", result.getUserName());
    }
}

