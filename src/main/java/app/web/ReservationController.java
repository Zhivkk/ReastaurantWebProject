package app.web;

import app.APIReservation.DTO.ReservationRequest;
import app.APIReservation.ReservationClient;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
class ReservationController {

    private final UserService userService;
    private final ReservationClient reservationClient;

    ReservationController(UserService userService, ReservationClient reservationClient) {
        this.userService = userService;
        this.reservationClient = reservationClient;
    }

    @GetMapping("/reservation")
    public ModelAndView getReservationPage(@AuthenticationPrincipal UserInfo userInfo) {
        User user = userService.getById(userInfo.getUserId());

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setUserName(user.getUsername());
        reservationRequest.setUserEmail(user.getEmail());
        reservationRequest.setUserPhone(user.getPhone());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("book-table");
        modelAndView.addObject("reservationRequest", reservationRequest);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/reservation")
    public String makeReservation (ReservationRequest reservationRequest) {
        reservationClient.makeReservation(reservationRequest);
        return "redirect:/home";
    }
}
