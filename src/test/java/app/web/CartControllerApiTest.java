package app.web;

import app.Cart.Cart;
import app.Cart.CartRepository;
import app.Errand.ErrandService;
import app.User.model.User;
import app.User.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ErrandService errandService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CartRepository cartRepository;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testCartId = UUID.randomUUID();
    private final UUID testErrandId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "CLIENT")
    void getCartPage_AuthenticatedClient_ReturnsCartView() throws Exception {
        // Arrange
        User mockUser = new User();
        Cart mockCart = new Cart();
        when(errandService.getAllCartsByUser(testUserId)).thenReturn(Collections.singletonList(mockCart));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));
        when(errandService.getTotalPrice(testUserId)).thenReturn(new BigDecimal("50.00"));

        // Act & Assert
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("carts", "totalPrice", "user"))
                .andExpect(model().attribute("totalPrice", new BigDecimal("50.00")));

        verify(errandService).getAllCartsByUser(testUserId);
        verify(userRepository).findById(testUserId);
        verify(errandService).getTotalPrice(testUserId);
    }
//
//    @Test
//    @WithMockUser(roles = "CLIENT")
//    void removeFromCart_ValidRequest_RedirectsToCart() throws Exception {
//        // Act & Assert
//        mockMvc.perform(delete("/cart/{id}/delete", testCartId)
//                        .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/cart"));
//
//        verify(errandService).removeFromCart(any(UserInfo.class), eq(testCartId));
//    }

    @Test
    @WithMockUser(roles = "CHEF")
    void getChefsCartPage_AuthenticatedChef_ReturnsCorrectView() throws Exception {
        // Arrange
        when(errandService.getCartsByErrandIdForChef(testErrandId))
                .thenReturn(Collections.singletonList(new Cart()));

        // Act & Assert
        mockMvc.perform(get("/chefCart/{id}", testErrandId))
                .andExpect(status().isOk())
                .andExpect(view().name("chef-cart"))
                .andExpect(model().attributeExists("carts"));
    }

    @Test
    @WithMockUser(roles = "CHEF")
    void finishedChefsCart_ValidRequest_RedirectsCorrectly() throws Exception {
        // Arrange
        String expectedErrandId = "errand-123";
        when(errandService.getErrandId(testErrandId)).thenReturn(expectedErrandId);

        // Act & Assert
        mockMvc.perform(post("/chefCart/{id}/finish", testErrandId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefCart/" + expectedErrandId));

        verify(errandService).checkStatus(testErrandId);
        verify(errandService).getErrandId(testErrandId);
    }

    @Test
    @WithMockUser(roles = "BARTENDER")
    void getBartenderCartPage_AuthenticatedBartender_ReturnsCorrectView() throws Exception {
        // Arrange
        when(errandService.getCartsByErrandIdForBartender(testErrandId))
                .thenReturn(Collections.singletonList(new Cart()));

        // Act & Assert
        mockMvc.perform(get("/bartenderCart/{id}", testErrandId))
                .andExpect(status().isOk())
                .andExpect(view().name("bartender-cart"))
                .andExpect(model().attributeExists("carts"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCartPage_UnauthorizedRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeFromCart_WithoutCsrf_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/cart/{id}/delete"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getCartPage_UserNotFound_ReturnsError() throws Exception {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/cart"))
                .andExpect(status().isNotFound());
    }
}
