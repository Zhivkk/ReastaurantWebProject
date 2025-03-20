package app.web;

import app.Cart.Cart;
import app.Errand.ErrandService;
import app.Security.UserInfo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CartController {

    private final ErrandService errandService;

    public CartController(ErrandService errandService) {
        this.errandService = errandService;
    }

    @GetMapping("/cart")
    public ModelAndView getCartPage(@AuthenticationPrincipal UserInfo userInfo) {

        List<Cart> carts = errandService.getAllCartsByUser(userInfo.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("cart");
        modelAndView.addObject("carts", carts);


        return modelAndView;

    }

}
