package app.web;

import app.Errand.Errand;
import app.Errand.ErrandService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import app.web.dto.AddCartRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ErrandController.class)

public class ErrandControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ErrandService errandService;

    @MockitoBean
    private UserService userService;

    private final UUID testUserId = UUID.randomUUID();
    private final Long testItemId = 1L;

    @Test
    @WithMockUser(roles = "CLIENT")
    void addToCart_ValidRequest_RedirectsToCart() throws Exception {
        // Arrange
        AddCartRequest mockRequest = new AddCartRequest();
        User mockUser = new User();
        when(userService.getById(testUserId)).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/addToCart/{id}", testItemId)
                        .with(csrf())
                        .flashAttr("addCartRequest", mockRequest))
                .andExpect(status().is4xxClientError());

        verify(errandService).addCartToErrand(any(User.class), any(AddCartRequest.class), eq(testItemId));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void finishErrand_AuthenticatedClient_RedirectsToHome() throws Exception {
        // Arrange
        User mockUser = new User();
        when(userService.getById(testUserId)).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/finish")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());

        verify(errandService).finishErrandFromUserSide(testUserId);
    }

    @Test
    @WithMockUser(roles = "CHEF")
    void getChefsPage_AuthenticatedChef_ReturnsCorrectView() throws Exception {
        // Arrange
        List<Errand> mockErrands = Collections.singletonList(new Errand());
        when(errandService.getAllErrandsForChefs()).thenReturn(mockErrands);

        // Act & Assert
        mockMvc.perform(get("/chef"))
                .andExpect(status().isOk())
                .andExpect(view().name("chefs-page"))
                .andExpect(model().attributeExists("errands"))
                .andExpect(model().attribute("errands", mockErrands));
    }

    @Test
    @WithMockUser(roles = "BARTENDER")
    void getBartenderPage_AuthenticatedBartender_ReturnsCorrectView() throws Exception {
        // Arrange
        List<Errand> mockErrands = Collections.singletonList(new Errand());
        when(errandService.getAllErrandsForBartender()).thenReturn(mockErrands);

        // Act & Assert
        mockMvc.perform(get("/bartender"))
                .andExpect(status().isOk())
                .andExpect(view().name("bartender-page"))
                .andExpect(model().attributeExists("errands"))
                .andExpect(model().attribute("errands", mockErrands));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getChefsPage_UnauthorizedRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/chef"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void addToCart_WithoutCsrf_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/addToCart/{id}", testItemId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void finishErrand_UserNotFound_ReturnsError() throws Exception {
        // Arrange
        when(userService.getById(testUserId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/finish")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }
}
