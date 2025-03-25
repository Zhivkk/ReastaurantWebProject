package app.web;

import app.Errand.Errand;
import app.Errand.ErrandService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.AddCartRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ErrandController {

    private final ErrandService errandService;
    private final UserService userService;

    public ErrandController(ErrandService errandService, UserService userService) {
        this.errandService = errandService;
        this.userService = userService;
    }

    @PreAuthorize( "hasRole('CLIENT')")
    @PostMapping("/addToCart/{id}")
    public String ErrandController(@PathVariable Long id,  @AuthenticationPrincipal UserInfo UserInfo, AddCartRequest addCartRequest) {

        User user = userService.getById(UserInfo.getUserId());
        errandService.addCartToErrand(user,addCartRequest, id);
        return "redirect:/cart";
    }

    @PreAuthorize( "hasRole('CLIENT')")
    @GetMapping("/finish")
    public String finishingTheErrandFromTheClientSite(@AuthenticationPrincipal UserInfo UserInfo) {

        User user = userService.getById(UserInfo.getUserId());
        errandService.finishErrandFromUserSide(user.getId());
        return "redirect:/home";
    }

    @PreAuthorize( "hasRole('CHEF')")
    @GetMapping("/chef")
    public ModelAndView getChefsPage(@AuthenticationPrincipal UserInfo userInfo) {

        List<Errand> errands= errandService.getAllErrandsForChefs();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("chefs-page");
        modelAndView.addObject("errands",errands);

        return modelAndView;
    }

    @PreAuthorize( "hasRole('BARTENDER')")
    @GetMapping("/bartender")
    public ModelAndView getBartenderPage(@AuthenticationPrincipal UserInfo userInfo) {

        List<Errand> errands= errandService.getAllErrandsForBartender();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bartender-page");
        modelAndView.addObject("errands",errands);

        return modelAndView;
    }

}
