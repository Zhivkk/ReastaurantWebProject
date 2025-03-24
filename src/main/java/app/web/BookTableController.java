package app.web;

import app.APIReservation.DTO.ReservationRequest;
import app.APIReservation.ReservationClient;
import app.APIReservation.ReservationService;
import app.Security.UserInfo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class BookTableController {

    private final ReservationClient reservationClient;
    private final ReservationService reservationService;

    public BookTableController(ReservationClient reservationClient, ReservationService reservationService) {
        this.reservationClient = reservationClient;
        this.reservationService = reservationService;
    }

    @GetMapping("/reservation")
    public ModelAndView BookTablePage(@AuthenticationPrincipal UserInfo userInfo){
    ReservationRequest reservationRequest = reservationService.addUserInfo(userInfo.getId());
    ModelAndView modelAndView = new ModelAndView();


    modelAndView.setViewName("book-table");
    modelAndView.addObject("reservationRequest",reservationRequest);

    return modelAndView;
    }

    @PostMapping("/reservation")
    public String makeReservation(ReservationRequest reservationRequest){
        reservationClient.makeReservation(reservationRequest);

        return "redirect:/home";
    }

}
