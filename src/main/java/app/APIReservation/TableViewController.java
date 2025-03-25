package app.APIReservation;

import app.APIReservation.DTO.TableDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/tables")
public class TableViewController {

    private final TableStatusService tableStatusService;

    public TableViewController(TableStatusService tableStatusService) {
        this.tableStatusService = tableStatusService;
    }

    @GetMapping("/reserved")
    public ModelAndView showReservedTables() {
        List<TableDTO> allReservations = tableStatusService.fetchAllReservations();
        ModelAndView mav = new ModelAndView("table-reservation"); // Указваме Thymeleaf темплейта
        mav.addObject("allReservations", allReservations);
        return mav;
    }
}


