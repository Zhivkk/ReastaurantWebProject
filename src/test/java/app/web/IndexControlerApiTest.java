package app.web;

import app.User.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class IndexControlerApiTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private IndexController indexController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();
    }

    @Test
    void getRegisterPage_ShouldReturnRegisterViewWithEmptyRequest() {
        ModelAndView result = indexController.getRegisterPage();

        assertEquals("register", result.getViewName());
        assertTrue(result.getModel().containsKey("registerRequest"));
        assertInstanceOf(RegisterRequest.class, result.getModel().get("registerRequest"));
    }

    @Test
    void getLoginPage_WithoutErrorParam_ShouldReturnBasicLoginView() {
        ModelAndView result = indexController.getLoginPage(null);
        assertEquals("login", result.getViewName());
        assertTrue(result.getModel().containsKey("loginRequest"));
        assertFalse(result.getModel().containsKey("errorMessage"));
    }

    @Test
    void getLoginPage_WithErrorParam_ShouldAddErrorMessage() {
        ModelAndView result = indexController.getLoginPage("error");

        assertEquals("login", result.getViewName());
        assertTrue(result.getModel().containsKey("errorMessage"));
        assertEquals("Incorrect username or password!", result.getModel().get("errorMessage"));
    }

    @Test
    void registerNewUser_WithValidRequest_ShouldRedirectToLogin() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenReturn(null);

        mockMvc.perform(post("/register")
                        .param("username", "validUser")
                        .param("email", "valid@example.com")
                        .param("password", "ValidPass123")
                        .param("phone", "1234567890"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).register(any(RegisterRequest.class));
    }

}
