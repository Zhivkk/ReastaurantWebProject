package app.web;

import app.Errand.Errand;
import app.Errand.ErrandService;
import app.User.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;
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
    @WithMockUser(roles = "CHEF")
    void getChefsPage_AuthenticatedChef_ReturnsCorrectView() throws Exception {

        List<Errand> mockErrands = Collections.singletonList(new Errand());
        when(errandService.getAllErrandsForChefs()).thenReturn(mockErrands);

        mockMvc.perform(get("/chef"))
                .andExpect(status().isOk())
                .andExpect(view().name("chefs-page"))
                .andExpect(model().attributeExists("errands"))
                .andExpect(model().attribute("errands", mockErrands));
    }

    @Test
    @WithMockUser(roles = "BARTENDER")
    void getBartenderPage_AuthenticatedBartender_ReturnsCorrectView() throws Exception {

        List<Errand> mockErrands = Collections.singletonList(new Errand());
        when(errandService.getAllErrandsForBartender()).thenReturn(mockErrands);

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

}
