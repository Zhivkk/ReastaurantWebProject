package app.APIReservation;

import app.APIMessage.MailRequest;
import app.APIReservation.DTO.ReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "reservationClient", url = "http://localhost:8082/reservations")
public interface ReservationClient {

    @PostMapping
    public void makeReservation(@RequestBody ReservationRequest reservationRequest);

}
