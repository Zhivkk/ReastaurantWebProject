package app.Message;

import app.Security.UserInfo;
import app.User.repository.UserRepository;
import app.web.dto.MessageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void addMessage(MessageRequest messageRequest) {

        Message message = Message.builder()
                .user(userRepository.findByUsername(messageRequest.getName()).orElse(null))
                .subject(messageRequest.getSubject())
                .messageText(messageRequest.getMessage())
                .messageStatus(MessageStaus.WRITEN)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        messageRepository.save(message);

    }
}
