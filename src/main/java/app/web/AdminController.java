package app.web;

import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.UserEditRequestAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String getAdminPage() {
    return "admin-page";
    }

    @GetMapping("/users/list")
    public ModelAndView UserListPage() {

        ModelAndView modelAndView = new ModelAndView();
        List<User> users= userService.getAllUsers();
        modelAndView.setViewName("users-list");
        modelAndView.addObject("users",users);

        return modelAndView;

    }

    @GetMapping("/users/{id}")
    public ModelAndView getUserEditPageAsAdmin( @PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(id);

        modelAndView.setViewName("admin-edit");
        modelAndView.addObject("userEditRequestAdmin", userService.mapUserToUserEditRequestAdmin(user));

        return modelAndView;
    }

    @PutMapping("/userEditAdmin")
    public String EditUserAdmin(UserEditRequestAdmin userEditRequestAdmin) {

        userService.editUserDetailsAdmin(userEditRequestAdmin);

        return "redirect:/users/list";
    }
}
