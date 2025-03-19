package app.Product;

import app.Ingredient.Ingredient;
import app.Ingredient.IngredientRepository;
import app.ProductIngredient.ProductIngredient;
import app.ProductIngredient.ProductIngredientRepository;
import app.ProductIngredient.ProductIngredientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ProductIngredientRepository productIngredientRepository;
    private IngredientRepository ingredientRepository;

    public ProductService(ProductRepository productRepository, ProductIngredientRepository productIngredientRepository, IngredientRepository ingredientRepository) {
        this.productRepository = productRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }


    public Product getById (Long id){

        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    public String getProductIngredients(Long id) {

        Product product = productRepository.findById(id).orElse(null);

        List<ProductIngredient> productIngredients = product.getProductIngredients();

        StringBuilder sb = new StringBuilder();

        productIngredients.stream().map(productIngredient -> productIngredient.getIngredient()
                .getName()).forEach(ingredientName -> sb.append(ingredientName).append(", "));

        return sb.toString();
    }
}
