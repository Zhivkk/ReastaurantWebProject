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
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    // Тестови обекти
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

    // Тест 1: Количката не се добавя при quantity <= 0
    @Test
    void addCartToErrand_QuantityZero_DoesNothing() {
        AddCartRequest request = new AddCartRequest();
        request.setQuantity(0);

        errandService.addCartToErrand(createUser(), request, 1L);

        verifyNoInteractions(productRepository, errandRepository, cartRepository);
    }

    // Тест 2: Грешка при липсващ продукт
    @Test
    void addCartToErrand_ProductNotFound_ThrowsException() {
        AddCartRequest request = new AddCartRequest();
        request.setQuantity(5);

        assertThatThrownBy(() -> errandService.addCartToErrand(createUser(), request, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }

//    // Тест 3: Създава нова поръчка и количка ако няма съществуваща
    @Test
    void addCartToErrand_NewErrand_CreatesErrandAndCart() {
        AddCartRequest request = new AddCartRequest();
        request.setQuantity(3);
        request.setProduct_id(1L);

        when(productRepository.findById(1L)).thenReturn(List.of());
        when(errandRepository.findByUserAndErrandStatus(createUser(), ErrandStatus.PREPARATION))
                .thenReturn(Collections.emptyList());

        errandService.addCartToErrand(createUser(), request, 1L);

        // Проверка за създаване на нова поръчка
        ArgumentCaptor<Errand> errandCaptor = ArgumentCaptor.forClass(Errand.class);
        verify(errandRepository).save(errandCaptor.capture());

        assertThat(errandCaptor.getValue())
                .satisfies(e -> {
                    assertThat(e.getUser()).isEqualTo(createUser());
                    assertThat(e.getErrandStatus()).isEqualTo(ErrandStatus.PREPARATION);
                    assertThat(e.getAddressForDelivery()).isEqualTo(createUser().getAddress());
                });

        // Проверка за създаване на количка
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());

        assertThat(cartCaptor.getValue())
                .satisfies(c -> {
                    assertThat(c.getProduct()).isEqualTo(createProduct(1L, ProductCategory.MAIN_COURSE));
                    assertThat(c.getQuantity()).isEqualTo(3);
                    assertThat(c.getErrand()).isEqualTo(errandCaptor.getValue());
                });
    }

//    // Тест 4: Използва съществуваща поръчка и добавя количка
    @Test
    void addCartToErrand_ExistingErrand_AddsCartToExistingErrand() {
        AddCartRequest request = new AddCartRequest();
        request.setQuantity(2);
        Errand existingErrand = Errand.builder()
                .user(createUser())
                .errandStatus(ErrandStatus.PREPARATION)
                .build();

        when(productRepository.findById(1L)).thenReturn(List.of());
        when(errandRepository.findByUserAndErrandStatus(createUser(), ErrandStatus.PREPARATION))
                .thenReturn(List.of(existingErrand));

        errandService.addCartToErrand(createUser(), request, 1L);

        // Проверка за използване на съществуваща поръчка
        verify(errandRepository).save(existingErrand); // Ако има логика за промяна

        // Проверка за нова количка
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());

        assertThat(cartCaptor.getValue())
                .satisfies(c -> {
                    assertThat(c.getErrand()).isEqualTo(existingErrand);
                    assertThat(c.getProduct()).isEqualTo(createProduct(1L, ProductCategory.MAIN_COURSE));
                });
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

     //Тестове за removeFromCart
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

     //Тестове за getTotalPrice
    @Test
    void getTotalPrice_WithMultipleItems_CalculatesCorrectTotal() {
        UUID userId = UUID.randomUUID();
        Cart cart1 = Cart.builder()
                .product(createProduct(1L, ProductCategory.MAIN_COURSE))
                .quantity(2)
                .build();

        Cart cart2 = Cart.builder()
                .product(createProduct(2L, ProductCategory.DESSERT))
                .quantity(3)
                .build();

        when(errandService.getAllCartsByUser(userId)).thenReturn(List.of(cart1, cart2));

        BigDecimal total = errandService.getTotalPrice(userId);

        assertEquals(BigDecimal.valueOf(50), total);
    }

    // Тестове за finishErrandFromUserSide
    @Test
    @Transactional
    void finishErrandFromUserSide_ValidErrand_UpdatesStatusAndBalance() {
        UUID userId = UUID.randomUUID();
        User user = createUser();
        Errand errand = Errand.builder()
                .errandStatus(ErrandStatus.PREPARATION)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(errandRepository.findByUserAndErrandStatus(user, ErrandStatus.PREPARATION))
                .thenReturn(List.of(errand));

        errandService.finishErrandFromUserSide(userId);

        assertEquals(ErrandStatus.FOR_EXECUTION, errand.getErrandStatus());
        verify(userRepository, times(1)).save(user);
    }

    // Тестове за getAllErrandsForChefs
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
    void checkStatus_AllCartsReady_UpdatesErrandStatus() {
        UUID cartId = UUID.randomUUID();
        Cart cart = Cart.builder()
                .id(cartId)
                .isReady(false)
                .errand(Errand.builder()
                        .carts(new ArrayList<>(List.of(new Cart())))
                        .build())
                .build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        assertTrue(cart.getIsReady());
        verify(errandRepository, times(1)).save(any(Errand.class));
    }

    // Тестове за методите на бармана
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

    // Тестове за методите за доставка
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

