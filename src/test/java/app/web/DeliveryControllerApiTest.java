package app.web;


import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import app.Errand.Errand;
import app.Errand.ErrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
public class DeliveryControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ErrandService errandService;

    private final UUID testErrandId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void deliveryPage_ShouldReturnErrandsList() throws Exception {

        Errand mockErrand = new Errand();
        when(errandService.getAllErrandsForDeliverry())
                .thenReturn(Collections.singletonList(mockErrand));

        mockMvc.perform(get("/delivery"))
                .andExpect(status().isOk())
                .andExpect(view().name("delivery"))
                .andExpect(model().attributeExists("errands"))
                .andExpect(model().attribute("errands", Collections.singletonList(mockErrand)));

        verify(errandService).getAllErrandsForDeliverry();
    }

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void finishDelivery_ValidRequest_ShouldRedirect() throws Exception {

        mockMvc.perform(put("/delivery/{id}/finish", testErrandId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/delivery"));

        verify(errandService).finishDeliverryStatus(testErrandId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deliveryPage_UnauthorizedRole_ShouldForbidAccess() throws Exception {
        mockMvc.perform(get("/delivery"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void finishDelivery_WithoutCsrf_ShouldBeForbidden() throws Exception {
        mockMvc.perform(put("/delivery/{id}/finish", testErrandId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void finishDelivery_WithInvalidId_ShouldProcessCorrectly() throws Exception {

        UUID invalidId = UUID.randomUUID();
        doNothing().when(errandService).finishDeliverryStatus(invalidId);

        mockMvc.perform(put("/delivery/{id}/finish", invalidId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(errandService).finishDeliverryStatus(invalidId);
    }
}

