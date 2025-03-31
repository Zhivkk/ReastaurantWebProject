package app.web;

import app.Message.MessageService;
import app.User.model.UserRole;
import app.User.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
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

        User mockUser = new User();
        mockUser.setId(TEST_USER_ID);
        mockUser.setUsername(TEST_USERNAME);
        mockUser.setEmail(TEST_EMAIL);

        when(userService.getById(TEST_USER_ID)).thenReturn(mockUser);

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

}
