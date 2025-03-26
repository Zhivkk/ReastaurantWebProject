package app.APIMessage;

import app.Security.UserInfo;
import app.User.model.User;
import app.User.repository.UserRepository;
import app.exception.UserDontExistException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MailService {

    private final MailClient mailClient;
    private final MailRepository mailRepository;
    private final UserRepository userRepository;

    public MailService(MailClient mailClient, MailRepository mailRepository, UserRepository userRepository) {
        this.mailClient = mailClient;
        this.mailRepository = mailRepository;
        this.userRepository = userRepository;
    }

    public void sendMail(@AuthenticationPrincipal UserInfo userInfo, MailRequest mailRequest) {
        mailClient.sendMail(mailRequest);

        User user = userRepository.findById(userInfo.getUserId()).orElseThrow(UserDontExistException::new);

        MailEntity mail = new MailEntity();
        mail.setRecipient(user.getEmail());
        mail.setSubject(mailRequest.getSubject());
        mail.setMessage(mailRequest.getBody());
        mail.setSentAt(LocalDateTime.now());

        mailRepository.save(mail);
    }
}
