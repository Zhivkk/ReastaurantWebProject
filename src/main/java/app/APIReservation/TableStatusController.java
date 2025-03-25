package app.APIReservation;

import app.APIReservation.DTO.TableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/external/tables")
public class TableStatusController {

    private final TableStatusService tableStatusService;

    public TableStatusController(TableStatusService tableStatusService) {
        this.tableStatusService = tableStatusService;
    }

    @GetMapping("/fetch-reserved-tables")
    public ResponseEntity<List<TableDTO>> getAllReservations() {
        List<TableDTO> reservations = tableStatusService.fetchAllReservations();
        return ResponseEntity.ok(reservations);
    }
}

