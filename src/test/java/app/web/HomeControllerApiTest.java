package app.web;

import app.Product.Product;
import app.Product.ProductCategory;
import app.Product.ProductRepository;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeControllerApiTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private HomeController homeController;

    @Test
    void getHomePage_ShouldReturnCorrectModelAndView() {

        UUID userId = UUID.randomUUID();
        UserInfo userInfo = mock(UserInfo.class);
        User mockUser = new User();

        when(userInfo.getUserId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(mockUser);

        List<Product> mockProducts = Collections.singletonList(new Product());
        for (ProductCategory category : ProductCategory.values()) {
            when(productRepository.findByProductCategory(category)).thenReturn(mockProducts);
        }


        ModelAndView result = homeController.getHomePage(userInfo);


        assertAll(
                () -> assertEquals("home", result.getViewName()),
                () -> assertSame(mockUser, result.getModel().get("user"))
        );

        verify(userService).getById(userId);
        for (ProductCategory category : ProductCategory.values()) {
            verify(productRepository).findByProductCategory(category);
        }

        assertAllCategoriesInModel(result);
    }

    @Test
    void getHomePage_ShouldHandleEmptyProductLists() {

        UUID userId = UUID.randomUUID();
        UserInfo userInfo = mock(UserInfo.class);
        when(userInfo.getUserId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(new User());

        when(productRepository.findByProductCategory(any())).thenReturn(Collections.emptyList());


        ModelAndView result = homeController.getHomePage(userInfo);


        assertAll(
                () -> assertTrue(((List<?>) result.getModel().get("soups")).isEmpty()),
                () -> assertTrue(((List<?>) result.getModel().get("specials")).isEmpty())
        );
    }

    private void assertAllCategoriesInModel(ModelAndView modelAndView) {
        assertAttributeExistsWithType(modelAndView, "soups", List.class);
        assertAttributeExistsWithType(modelAndView, "sallads", List.class);
        assertAttributeExistsWithType(modelAndView, "appetizers", List.class);
        assertAttributeExistsWithType(modelAndView, "mainCourses", List.class);
        assertAttributeExistsWithType(modelAndView, "desserts", List.class);
        assertAttributeExistsWithType(modelAndView, "softDrinks", List.class);
        assertAttributeExistsWithType(modelAndView, "alcohols", List.class);
        assertAttributeExistsWithType(modelAndView, "others", List.class);
        assertAttributeExistsWithType(modelAndView, "specials", List.class);
    }

    private void assertAttributeExistsWithType(ModelAndView modelAndView, String attributeName, Class<?> type) {
        assertTrue(
                modelAndView.getModel().containsKey(attributeName),
                "Missing attribute: " + attributeName
        );
        assertTrue(
                type.isInstance(modelAndView.getModel().get(attributeName)),
                "Wrong type for attribute: " + attributeName
        );
    }
}
