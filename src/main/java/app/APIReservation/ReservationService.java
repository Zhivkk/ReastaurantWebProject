package app.APIReservation;

import app.APIReservation.DTO.ReservationRequest;
import app.User.model.User;
import app.User.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReservationService {

    private final UserService userService;

    public ReservationService(UserService userService) {
        this.userService = userService;
    }

    public ReservationRequest addUserInfo(UUID id) {
        User user=  userService.getById(id);
        ReservationRequest reservationRequest = new ReservationRequest();

        reservationRequest.setUserPhone(user.getPhone());
        reservationRequest.setUserEmail(user.getEmail());
        reservationRequest.setUserName(user.getUsername());

        return reservationRequest;
    }
}
