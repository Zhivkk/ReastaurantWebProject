package app.web;

import app.User.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        // Act
        ModelAndView result = indexController.getRegisterPage();

        // Assert
        assertEquals("register", result.getViewName());
        assertTrue(result.getModel().containsKey("registerRequest"));
        assertInstanceOf(RegisterRequest.class, result.getModel().get("registerRequest"));
    }

    @Test
    void getLoginPage_WithoutErrorParam_ShouldReturnBasicLoginView() {
        // Act
        ModelAndView result = indexController.getLoginPage(null);

        // Assert
        assertEquals("login", result.getViewName());
        assertTrue(result.getModel().containsKey("loginRequest"));
        assertFalse(result.getModel().containsKey("errorMessage"));
    }

    @Test
    void getLoginPage_WithErrorParam_ShouldAddErrorMessage() {
        // Act
        ModelAndView result = indexController.getLoginPage("error");

        // Assert
        assertEquals("login", result.getViewName());
        assertTrue(result.getModel().containsKey("errorMessage"));
        assertEquals("Incorrect username or password!", result.getModel().get("errorMessage"));
    }

//    @Test
//    void getRegisterPage_ShouldReturnRegisterView() throws Exception {
//        mockMvc.perform(get("/register"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("register"))
//                .andExpect(model().attributeExists("registerRequest"));
//    }

//    @Test
//    void registerNewUser_WithValidationErrors_ShouldReturnRegisterView() throws Exception {
//        mockMvc.perform(post("/register")
//                        .param("username", "short")
//                        .param("email", "invalid")
//                        .param("password", "short")
//                        .param("phone", "123"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("register"))
//                .andExpect(model().attributeHasErrors("registerRequest"));
//    }

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

//    @Test
//    void getLoginPage_WithoutError_ShouldReturnLoginView() throws Exception {
//        mockMvc.perform(get("/login"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("login"))
//                .andExpect(model().attributeExists("loginRequest"))
//                .andExpect(model().attributeDoesNotExist("errorMessage"));
//    }

//    @Test
//    void getLoginPage_WithError_ShouldShowErrorMessage() throws Exception {
//        mockMvc.perform(get("/login").param("error", "true"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("login"))
//                .andExpect(model().attributeExists("errorMessage"))
//                .andExpect(model().attribute("errorMessage", "Incorrect username or password!"));
//    }

}
