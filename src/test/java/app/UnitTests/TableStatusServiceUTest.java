package app.UnitTests;

import app.APIReservation.DTO.TableDTO;
import app.APIReservation.ReservationClient;
import app.APIReservation.TableStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableStatusServiceTest {

    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private TableStatusService tableStatusService;

    @Test
    void fetchAllReservations_ReturnsListFromClient() {
        // Given
        List<TableDTO> expectedReservations = List.of(
                new TableDTO(),
                new TableDTO()
        );

        when(reservationClient.getAllReservations()).thenReturn(expectedReservations);

        // When
        List<TableDTO> result = tableStatusService.fetchAllReservations();

        // Then
        verify(reservationClient, times(1)).getAllReservations();
        assertEquals(expectedReservations, result);
    }

    @Test
    void fetchAllReservations_WhenClientReturnsEmptyList_ReturnsEmptyList() {
        // Given
        when(reservationClient.getAllReservations()).thenReturn(Collections.emptyList());

        // When
        List<TableDTO> result = tableStatusService.fetchAllReservations();

        // Then
        assertTrue(result.isEmpty());
        verify(reservationClient, times(1)).getAllReservations();
    }

    @Test
    void fetchAllReservations_WhenClientThrowsException_PropagatesException() {
        // Given
        when(reservationClient.getAllReservations()).thenThrow(new RuntimeException("API Error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> tableStatusService.fetchAllReservations()
        );
    }
}
