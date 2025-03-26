package app.Message;

import app.Security.UserInfo;
import app.User.repository.UserRepository;
import app.web.dto.MessageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
                .user(userRepository.findByUsername(messageRequest.getName()).orElseThrow(RuntimeException::new))
                .subject(messageRequest.getSubject())
                .messageText(messageRequest.getMessage())
                .messageStatus(MessageStaus.WRITEN)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        messageRepository.save(message);

    }

    public List<Message> getAllMessages() {
        return messageRepository.findAllByMessageStatus(MessageStaus.WRITEN);
    }

    public Message getMessageById(UUID id) {

        Message message = messageRepository.findById(id).orElseThrow(RuntimeException::new);
        message.setMessageStatus(MessageStaus.READ);
        messageRepository.save(message);

        return message;
    }

    public String getUserEmail(UUID id) {

        return messageRepository.findById(id).orElseThrow(RuntimeException::new).getUser().getEmail();

    }
}
