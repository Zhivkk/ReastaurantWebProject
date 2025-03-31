package app.web;

import app.Message.Message;
import app.Message.MessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminMessageController {

    private final MessageService messageService;

    public AdminMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize( "hasRole('ADMIN')")
    @GetMapping("/message/list")
    public ModelAndView UserListPage() {

        ModelAndView modelAndView = new ModelAndView();
        List<Message>message = messageService.getAllMessages();

        modelAndView.setViewName("admin-message-list");
        modelAndView.addObject("messages",message);

        return modelAndView;

    }

    @PreAuthorize( "hasRole('ADMIN')")
    @GetMapping("/messages/{id}")
    public ModelAndView MessageFromUser(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView();
        Message message = messageService.getMessageById(id);
        String email = messageService.getUserEmail(id);

        modelAndView.setViewName("admin-message-view");
        modelAndView.addObject("message",message);
        modelAndView.addObject("email",email);

        return modelAndView;
    }

}
