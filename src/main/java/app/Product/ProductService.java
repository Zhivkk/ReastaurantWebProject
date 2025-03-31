package app.Product;

import app.Ingredient.IngredientRepository;
import app.ProductIngredient.ProductIngredient;
import app.ProductIngredient.ProductIngredientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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

        Product product = productRepository.findById(id).orElseThrow(RuntimeException::new);
        return product;
    }

    public String getProductIngredients(Long id) {

        Product product = productRepository.findById(id).orElseThrow(RuntimeException::new);

        List<ProductIngredient> productIngredients = product.getProductIngredients();

        StringBuilder sb = new StringBuilder();

        productIngredients.stream().map(productIngredient -> productIngredient.getIngredient()
                .getName()).forEach(ingredientName -> sb.append(ingredientName).append(", "));

        return sb.toString();
    }
}
