package app.APIReservation;

import app.APIReservation.DTO.ReservationRequest;
import app.APIReservation.DTO.TableDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "reservationClient", url = "http://localhost:8082/reservations")
public interface ReservationClient {

    @PostMapping
    public void makeReservation(@RequestBody ReservationRequest reservationRequest);


    @GetMapping("/reserved-tables")
    List<TableDTO> getAllReservations();


}
