package app.web;

import app.Message.MessageService;
import app.User.model.UserRole;
import app.User.service.UserService;
import app.web.dto.MessageRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import app.Security.UserInfo;
import app.User.model.User;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(MessageController.class)
public class MessageControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MessageService messageService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_EMAIL = "test@abv.bg";

    @Test
    @WithMockUser
    void getMessagesPage_AuthenticatedUser_ReturnsFormWithUserData() throws Exception {
        // Mock user data with UUID
        User mockUser = new User();
        mockUser.setId(TEST_USER_ID);
        mockUser.setUsername(TEST_USERNAME);
        mockUser.setEmail(TEST_EMAIL);

        when(userService.getById(TEST_USER_ID)).thenReturn(mockUser);

        // Execute and verify
        mockMvc.perform(get("/messages")
                        .with(user(new UserInfo(TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, UserRole.CLIENT, true))))
                .andExpect(status().isOk())
                .andExpect(view().name("messages"))
                .andExpect(model().attributeExists("messageRequest"))
                .andExpect(model().attribute("messageRequest",
                        hasProperty("name", equalTo(TEST_USERNAME))))
                .andExpect(model().attribute("messageRequest",
                        hasProperty("email", equalTo(TEST_EMAIL))));
    }

    @Test
    void postMessage_ValidRequest_RedirectsToHome() throws Exception {
        // Test data with UUID user context
        String testMessage = "Test message with UUID user";
        UserInfo userInfo = new UserInfo(TEST_USER_ID, TEST_USERNAME, TEST_EMAIL, UserRole.CLIENT, true);
        userInfo.setUserId(TEST_USER_ID);

        // Execute POST request
        mockMvc.perform(post("/messages")
                        .param("name", "TestUser")
                        .param("email", "test@abv.bg")
                        .param("message", testMessage)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        // Verify service call with UUID context
        ArgumentCaptor<MessageRequest> captor = ArgumentCaptor.forClass(MessageRequest.class);
        verify(messageService).addMessage(captor.capture());

        MessageRequest captured = captor.getValue();
        assertEquals("TestUser", captured.getName());
        assertEquals("test@abv.bg", captured.getEmail());
        assertEquals(testMessage, captured.getMessage());
    }

    @Test
    void postMessage_InvalidData_ReturnsErrors() throws Exception {
        // Test invalid data
        mockMvc.perform(post("/messages")
                        .param("name", "")
                        .param("email", "invalid-email")
                        .param("message", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(
                        "messageRequest", "name", "email", "message"));
    }
}
