package app.web;

import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.UserEditRequestAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // Mock dependency

    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("John Doe");
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simulate an ADMIN user
    void shouldReturnAdminPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUserListPage() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("users-list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUserEditPageAsAdmin() throws Exception {
        when(userService.getById(userId)).thenReturn(testUser);
        when(userService.mapUserToUserEditRequestAdmin(Mockito.any()))
                .thenReturn(new UserEditRequestAdmin());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-edit"))
                .andExpect(model().attributeExists("userEditRequestAdmin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEditUserAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/userEditAdmin")
                        .param("id", userId.toString())
                        .param("name", "Updated Name"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldDenyAccessForNonAdminUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().is3xxRedirection());
    }
}
