package app.UnitTests;

import app.Cart.Cart;
import app.Cart.CartRepository;
import app.Errand.Errand;
import app.Errand.ErrandRepository;
import app.Errand.ErrandService;
import app.Errand.ErrandStatus;
import app.Product.Product;
import app.Product.ProductCategory;
import app.Product.ProductRepository;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.model.UserRole;
import app.User.repository.UserRepository;
import app.web.dto.AddCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrandServiceTest {

    @Mock private ErrandRepository errandRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ErrandService errandService;

    private User createUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .address("Test Address")
                .accountAmount(BigDecimal.valueOf(1000))
                .build();
    }

    private Product createProduct(Long id, ProductCategory category) {
        return Product.builder()
                .id(id)
                .productName("Test Product")
                .price(BigDecimal.TEN)
                .productCategory(category)
                .build();
    }

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setAddress("Test Address");
        Product product = new Product();
        product.setId(1L);
    }

    @Test
    void addCartToErrand_QuantityZero_DoesNothing() {
        AddCartRequest request = new AddCartRequest();
        request.setQuantity(0);

        errandService.addCartToErrand(createUser(), request, 1L);

        verifyNoInteractions(productRepository, errandRepository, cartRepository);
    }

    @Test
    void addCartToErrand_ProductNotFound_ThrowsException() {
        AddCartRequest request = new AddCartRequest();
        request.setQuantity(5);

        assertThatThrownBy(() -> errandService.addCartToErrand(createUser(), request, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void getAllCartsByUser_WithExistingErrand_ReturnsCarts() {
        UUID userId = UUID.randomUUID();
        User user = createUser();
        Errand errand = Errand.builder()
                .carts(new ArrayList<>(List.of(new Cart(), new Cart())))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION))
                .thenReturn(List.of(errand));

        List<Cart> result = errandService.getAllCartsByUser(userId);

        assertEquals(2, result.size());
    }


    @Test
    void removeFromCart_ValidCartId_DeletesCart() {
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "User123", "123123", UserRole.CLIENT, true);
        User user = createUser();
        Errand errand = Errand.builder()
                .carts(new ArrayList<>(List.of(Cart.builder().id(UUID.randomUUID()).build())))
                .build();

        when(userRepository.findById(userInfo.getUserId())).thenReturn(Optional.of(user));
        when(errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION))
                .thenReturn(List.of(errand));

        errandService.removeFromCart(userInfo, errand.getCarts().get(0).getId());

        verify(cartRepository, times(1)).delete(any(Cart.class));
    }


    @Test
    void getAllErrandsForChefs_FiltersCorrectly() {
        Errand validErrand = createErrandWithCarts(ProductCategory.MAIN_COURSE, false);
        Errand invalidErrand = createErrandWithCarts(ProductCategory.ALCOHOL, false);

        when(errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION))
                .thenReturn(List.of(validErrand, invalidErrand));

        List<Errand> result = errandService.getAllErrandsForChefs();

        assertEquals(1, result.size());
    }

    private Errand createErrandWithCarts(ProductCategory category, boolean isReady) {
        Cart cart = Cart.builder()
                .product(createProduct(1L, category))
                .isReady(isReady)
                .build();
        return Errand.builder()
                .carts(List.of(cart))
                .build();
    }


    @Test
    void getAllErrandsForBartender_FiltersDrinks() {
        Errand validErrand = createErrandWithCarts(ProductCategory.ALCOHOL, false);
        Errand validErrand2 = createErrandWithCarts(ProductCategory.SOFT_DRINK, false);
        Errand invalidErrand = createErrandWithCarts(ProductCategory.MAIN_COURSE, true);

        when(errandRepository.findByErrandStatus(ErrandStatus.FOR_EXECUTION))
                .thenReturn(List.of(validErrand, validErrand2, invalidErrand));

        List<Errand> result = errandService.getAllErrandsForBartender();

        assertEquals(2, result.size());
        assertTrue(result.contains(validErrand));
        assertTrue(result.contains(validErrand2));
        assertFalse(result.contains(invalidErrand));
    }

    @Test
    void finishDeliverryStatus_ValidErrand_UpdatesStatus() {
        UUID errandId = UUID.randomUUID();
        Errand errand = Errand.builder()
                .errandStatus(ErrandStatus.FOR_DELIVERY)
                .id(errandId)
                .build();

        when(errandRepository.findById(errandId)).thenReturn(Optional.of(errand));

        errandService.finishDeliverryStatus(errandId);

        assertEquals(ErrandStatus.DELIVERED, errand.getErrandStatus());
        verify(errandRepository, times(1)).save(errand);
    }
}

