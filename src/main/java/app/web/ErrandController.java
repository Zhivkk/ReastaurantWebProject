package app.web;

import app.Errand.ErrandService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.AddCartRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ErrandController {

    private final ErrandService errandService;
    private final UserService userService;

    public ErrandController(ErrandService errandService, UserService userService) {
        this.errandService = errandService;
        this.userService = userService;
    }

    @PostMapping("/addToCart/{id}")
    public String ErrandController(@PathVariable Long id,  @AuthenticationPrincipal UserInfo UserInfo, AddCartRequest addCartRequest) {

        User user = userService.getById(UserInfo.getUserId());

        errandService.addCartToErrand(user,addCartRequest, id);

        return "redirect:/cart";
    }

}
