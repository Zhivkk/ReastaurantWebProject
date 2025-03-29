package app.UnitTests;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.Message.Message;
import app.Message.MessageRepository;
import app.Message.MessageService;
import app.Message.MessageStaus;
import app.User.model.User;
import app.User.repository.UserRepository;
import app.web.dto.MessageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MessageServiceUTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void addMessage_ValidRequest_SavesMessage() {
        // Arrange
        MessageRequest request = new MessageRequest("testUser", "mail", "Subject", "Message");
        User mockUser = new User();
        mockUser.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        // Act
        messageService.addMessage(request);

        // Assert
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());

        Message savedMessage = messageCaptor.getValue();
        assertThat(savedMessage.getUser()).isEqualTo(mockUser);
        assertThat(savedMessage.getSubject()).isEqualTo("Subject");
        assertThat(savedMessage.getMessageText()).isEqualTo("Message");
        assertThat(savedMessage.getMessageStatus()).isEqualTo(MessageStaus.WRITEN);
        assertThat(savedMessage.getCreatedOn()).isNotNull();
    }

    @Test
    void addMessage_UserNotFound_ThrowsException() {
        // Arrange
        MessageRequest request = new MessageRequest("nonExistingUser", "mail", "Subject", "Message");
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.addMessage(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAllMessages_ReturnsOnlyWrittenMessages() {
        // Arrange
        Message message1 = createTestMessage(MessageStaus.WRITEN);
        Message message2 = createTestMessage(MessageStaus.READ);
        when(messageRepository.findAllByMessageStatus(MessageStaus.WRITEN)).thenReturn(List.of(message1));

        // Act
        List<Message> result = messageService.getAllMessages();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessageStatus()).isEqualTo(MessageStaus.WRITEN);
    }

    @Test
    void getMessageById_UpdatesStatusToRead() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        Message mockMessage = createTestMessage(MessageStaus.WRITEN);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(mockMessage));

        // Act
        Message result = messageService.getMessageById(messageId);

        // Assert
        assertThat(result.getMessageStatus()).isEqualTo(MessageStaus.READ);
        verify(messageRepository).save(mockMessage);
    }

    @Test
    void getMessageById_InvalidId_ThrowsException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();
        when(messageRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> messageService.getMessageById(invalidId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getUserEmail_ReturnsCorrectEmail() {
        // Arrange
        UUID messageId = UUID.randomUUID();
        User user = new User();
        user.setEmail("test@example.com");
        Message mockMessage = createTestMessage(user);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(mockMessage));

        // Act
        String email = messageService.getUserEmail(messageId);

        // Assert
        assertThat(email).isEqualTo("test@example.com");
    }

    private Message createTestMessage(MessageStaus status) {
        return Message.builder()
                .user(new User())
                .subject("Test")
                .messageText("Content")
                .messageStatus(status)
                .createdOn(LocalDateTime.now())
                .build();
    }

    private Message createTestMessage(User user) {
        return Message.builder()
                .user(user)
                .subject("Test")
                .messageText("Content")
                .messageStatus(MessageStaus.WRITEN)
                .build();
    }
}

