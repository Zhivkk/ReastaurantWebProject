package app.web;

import app.Errand.Errand;
import app.Errand.ErrandService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class DeliveryController {

    private final ErrandService errandService;

    public DeliveryController(ErrandService errandService) {
        this.errandService = errandService;
    }

    @GetMapping("/delivery")
    public ModelAndView DeliveryPage() {

        ModelAndView modelAndView = new ModelAndView();
        List<Errand> errands = errandService.getAllErrandsForDeliverry();

        modelAndView.setViewName("delivery");
        modelAndView.addObject("errands", errands);

        return modelAndView;

    }

    @PutMapping("/delivery/{id}/finish")
    public String finnishDelivery(@PathVariable UUID id) {

        errandService.finishDeliverryStatus(id);

        return "redirect:/delivery";

    }

}
