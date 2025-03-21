package app.web;

import app.Cart.Cart;
import app.Errand.ErrandService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class CartController {

    private final ErrandService errandService;
    private final UserRepository userRepository;

    public CartController(ErrandService errandService, UserRepository userRepository) {
        this.errandService = errandService;
        this.userRepository = userRepository;
    }

    @GetMapping("/cart")
    public ModelAndView getCartPage(@AuthenticationPrincipal UserInfo userInfo) {

        List<Cart> carts = errandService.getAllCartsByUser(userInfo.getUserId());

        User user = userRepository.findById(userInfo.getUserId()).get();

        BigDecimal totalPrice = new BigDecimal(0);
        totalPrice=errandService.getTotalPrice(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("cart");
        modelAndView.addObject("carts", carts);
        modelAndView.addObject("totalPrice",totalPrice);
        modelAndView.addObject("user",user);


        return modelAndView;

    }

    @DeleteMapping("/cart/{id}/delete")
    public String removeFromCart(@AuthenticationPrincipal UserInfo userInfo, @PathVariable UUID id) {

        errandService.removeFromCart(userInfo,id);

        return "redirect:/cart";

    }



}
