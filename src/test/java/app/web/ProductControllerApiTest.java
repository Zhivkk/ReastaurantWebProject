package app.web;

import app.Errand.ErrandRepository;
import app.Product.Product;
import app.Product.ProductService;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.model.UserRole;
import app.User.repository.UserRepository;
import app.web.dto.AddCartRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerApiTest {

    @Mock
    private ProductService productService;

    @Mock
    private ErrandRepository errandRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductController productController;

    @Test
    void getProductPage_ReturnsCorrectModelAndView() {

        Long productId = 1L;
        UUID userId = UUID.randomUUID();
        UserInfo userInfo = new UserInfo(userId, "test@example.com", "ROLE_USER", UserRole.CLIENT, true);

        Product mockProduct = new Product();
        mockProduct.setId(productId);
        String ingredients = "Ingredient1, Ingredient2";
        User mockUser = new User();
        mockUser.setId(userId);

        when(productService.getById(productId)).thenReturn(mockProduct);
        when(productService.getProductIngredients(productId)).thenReturn(ingredients);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        ModelAndView result = productController.getProductPage(productId, userInfo);

        assertEquals("product-details", result.getViewName());

        assertAll(
                () -> assertEquals(mockProduct, result.getModel().get("product")),
                () -> assertEquals(ingredients, result.getModel().get("ingredients")),
                () -> assertEquals(mockUser, result.getModel().get("user")),
                () -> assertTrue(result.getModel().containsKey("addCartRequest"))
        );

        AddCartRequest cartRequest = (AddCartRequest) result.getModel().get("addCartRequest");
        assertEquals(1, cartRequest.getQuantity());

        verify(productService).getById(productId);
        verify(productService).getProductIngredients(productId);
        verify(userRepository).findById(userId);
    }
}
