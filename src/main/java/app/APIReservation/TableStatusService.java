package app.APIReservation;

import app.APIReservation.DTO.TableDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableStatusService {

    private final ReservationClient reservationClient;

    public TableStatusService(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;
    }

    public List<TableDTO> fetchAllReservations() {
        return reservationClient.getAllReservations();
    }
}

