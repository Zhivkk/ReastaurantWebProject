package app.web;

import app.Message.Message;
import app.Message.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMessageController.class)
@Import(AdminMessageControllerApiTest.TestConfig.class)
class AdminMessageControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageService messageService;

    private final UUID testMessageId = UUID.randomUUID();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MessageService messageService() {
            return Mockito.mock(MessageService.class);
        }
    }

    // ---------------------- UserListPage() ----------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void getMessageList_authorized_returnsMessageList() throws Exception {
        List<Message> mockMessages = List.of(
                new Message(),
                new Message()
        );

        when(messageService.getAllMessages()).thenReturn(mockMessages);

        mockMvc.perform(get("/message/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-message-list"))
                .andExpect(model().attributeExists("messages"))
                .andExpect(model().attribute("messages", mockMessages));

        verify(messageService).getAllMessages();
    }

    @Test
    void getMessageList_unauthorized_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/message/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // ---------------------- MessageFromUser() ----------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void getMessageDetails_validId_returnsMessageView() throws Exception {
        Message mockMessage = new Message();
        String mockEmail = "user@example.com";

        // Мокване за произволни UUID стойности
        when(messageService.getMessageById(any(UUID.class))).thenReturn(mockMessage);
        when(messageService.getUserEmail(any(UUID.class))).thenReturn(mockEmail);

        mockMvc.perform(get("/messages/{id}", testMessageId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-message-view"))
                .andExpect(model().attribute("message", mockMessage))
                .andExpect(model().attribute("email", mockEmail));

        // Допълнителна проверка за подадените параметри
        verify(messageService).getMessageById(testMessageId);
        verify(messageService).getUserEmail(testMessageId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getMessageDetails_invalidId_returnsNotFound() throws Exception {
        when(messageService.getMessageById(any(UUID.class)))
                .thenThrow(new RuntimeException("Message not found"));

        mockMvc.perform(get("/messages/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(messageService).getMessageById(any(UUID.class));
    }

    @Test
    void getMessageDetails_unauthorized_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/messages/{id}", testMessageId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}

