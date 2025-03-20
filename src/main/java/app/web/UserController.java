package app.web;

import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/userEdit")
//    public ModelAndView getRegisterPage() {
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("userEdit");
//        modelAndView.addObject("userEditRequest", new UserEditRequest());
//
//        return modelAndView;
//    }

    @PostMapping("/userEdit")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }
        userService.register(registerRequest);

        return new ModelAndView("redirect:/login");
    }
}
