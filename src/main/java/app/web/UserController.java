package app.web;

import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.UserEditRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}/edit")

    public ModelAndView getRegisterPage(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getById(id);

        modelAndView.setViewName("user-edit");
        modelAndView.addObject("userEditRequest",userService.mapUserToUserEditRequest(user));
        return modelAndView;
    }

    @PostMapping("/userEdit")
    public ModelAndView registerNewUser(@AuthenticationPrincipal UserInfo userInfo,  @Valid  UserEditRequest userEditRequest, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new ModelAndView("user-edit");
        }

        userService.editUserDetails(userInfo.getUserId(),userEditRequest);

        return new ModelAndView("redirect:/home");
    }
}
