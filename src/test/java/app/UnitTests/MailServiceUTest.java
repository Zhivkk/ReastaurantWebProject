package app.UnitTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.util.UUID;
import app.APIMessage.MailClient;
import app.APIMessage.MailRepository;
import app.APIMessage.MailRequest;
import app.APIMessage.MailService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.model.UserRole;
import app.User.repository.UserRepository;
import app.exception.UserDontExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
public class MailServiceUTest {

    @Mock
    private MailClient mailClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailRepository mailRepository;

    @InjectMocks
    private MailService mailService;

    private UserInfo userInfo;
    private MailRequest mailRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userInfo = new UserInfo(UUID.randomUUID(), "testUser","pasword", UserRole.CLIENT, true);
        mailRequest = new MailRequest("Subject", "Hello, this is a test email.","hello world");
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
    }

    @Test
    void sendMail_ShouldSendMailAndSaveMailEntity() {

        when(userRepository.findById(userInfo.getUserId())).thenReturn(Optional.of(user));


        mailService.sendMail(userInfo, mailRequest);


        verify(mailClient).sendMail(mailRequest);
        verify(mailRepository).save(argThat(mail ->
                mail.getRecipient().equals(user.getEmail()) &&
                        mail.getSubject().equals(mailRequest.getSubject()) &&
                        mail.getMessage().equals(mailRequest.getBody()) &&
                        mail.getSentAt() != null
        ));
    }

    @Test
    void sendMail_ShouldThrowException_WhenUserNotFound() {

        when(userRepository.findById(userInfo.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserDontExistException.class, () ->
                mailService.sendMail(userInfo, mailRequest)
        );

        verify(mailRepository, never()).save(any());
    }

}
