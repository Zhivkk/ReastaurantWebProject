package app.web;

import app.Cart.CartRepository;
import app.Errand.ErrandService;
import app.User.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
    @WithMockUser(roles = "CHEF")
    void finishedChefsCart_ValidRequest_RedirectsCorrectly() throws Exception {

        String expectedErrandId = "errand-123";
        when(errandService.getErrandId(testErrandId)).thenReturn(expectedErrandId);

        mockMvc.perform(post("/chefCart/{id}/finish", testErrandId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefCart/" + expectedErrandId));

        verify(errandService).checkStatus(testErrandId);
        verify(errandService).getErrandId(testErrandId);
    }


    @Test
    @WithMockUser(roles = "CLIENT")
    void getCartPage_UserNotFound_ReturnsError() throws Exception {

        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/cart"))
                .andExpect(status().isNotFound());
    }
}
