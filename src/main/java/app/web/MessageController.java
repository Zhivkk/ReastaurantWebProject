package app.web;

import app.Message.MessageService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.MessageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("/messages")
    public ModelAndView getMessagesPage(@AuthenticationPrincipal UserInfo userInfo) {

        User user = userService.getById(userInfo.getUserId());
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setName(user.getUsername());
        messageRequest.setEmail(user.getEmail());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("messages");
        modelAndView.addObject("messageRequest", messageRequest);

        return modelAndView;
    }

    @PostMapping("/messages")
    public String MessagesP( MessageRequest messageRequest) {

        messageService.addMessage ( messageRequest);

        return "redirect:/home";
    }
}
