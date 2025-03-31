package app.UnitTests;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import app.Ingredient.Ingredient;
import app.Product.Product;
import app.Product.ProductRepository;
import app.Product.ProductService;
import app.ProductIngredient.ProductIngredient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceUTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getById_ValidId_ReturnsProduct() {

        Long productId = 1L;
        Product mockProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));


        Product result = productService.getById(productId);


        assertThat(result).isEqualTo(mockProduct);
    }

    @Test
    void getById_InvalidId_ThrowsException() {

        Long invalidId = 999L;
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> productService.getById(invalidId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getProductIngredients_ValidProduct_ReturnsIngredientsList() {

        Long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Salt");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Sugar");

        ProductIngredient pi1 = new ProductIngredient();
        pi1.setIngredient(ingredient1);
        ProductIngredient pi2 = new ProductIngredient();
        pi2.setIngredient(ingredient2);

        mockProduct.setProductIngredients(List.of(pi1, pi2));

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));


        String result = productService.getProductIngredients(productId);


        assertThat(result).isEqualTo("Salt, Sugar, ");
    }

    @Test
    void getProductIngredients_NoIngredients_ReturnsEmptyString() {

        Long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setProductIngredients(List.of());

        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));


        String result = productService.getProductIngredients(productId);


        assertThat(result).isEmpty();
    }

    @Test
    void getProductIngredients_ProductNotFound_ThrowsException() {

        Long invalidId = 999L;
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> productService.getProductIngredients(invalidId))
                .isInstanceOf(RuntimeException.class);
    }
}

