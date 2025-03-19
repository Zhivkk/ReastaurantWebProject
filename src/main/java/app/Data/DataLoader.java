package app.Data;

import app.Ingredient.Ingredient;
import app.Ingredient.IngredientRepository;
import app.Product.Product;
import app.Product.ProductRepository;
import app.ProductIngredient.ProductIngredient;
import app.ProductIngredient.ProductIngredientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String GITHUB_RAW_URL = "https://raw.githubusercontent.com/Zhivkk/RestaurantWebProjectResources/main/jsons/";

    public DataLoader(IngredientRepository ingredientRepository, ProductRepository productRepository,
                      ProductIngredientRepository productIngredientRepository, ObjectMapper objectMapper) {
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        loadIngredients();
        loadProducts();
        loadProductIngredients();
    }

    private void loadIngredients() throws Exception {
        String url = GITHUB_RAW_URL + "ingredients.json";
        String json = restTemplate.getForObject(url, String.class);
        List<Ingredient> ingredients = objectMapper.readValue(json, new TypeReference<>() {});
        ingredientRepository.saveAll(ingredients);
    }

    private void loadProducts() throws Exception {
        String url = GITHUB_RAW_URL + "products.json";
        String json = restTemplate.getForObject(url, String.class);
        List<Product> products = objectMapper.readValue(json, new TypeReference<>() {});
        productRepository.saveAll(products);
    }

    private void loadProductIngredients() throws Exception {
        String url = GITHUB_RAW_URL + "product_ingredients.json";
        String json = restTemplate.getForObject(url, String.class);

        // Парсиране на JSON-а към списък от DTO обекти
        List<ProductIngredientDTO> dtoList = objectMapper.readValue(json, new TypeReference<>() {});

        // Преобразуване на DTO обектите към ProductIngredient
        for (ProductIngredientDTO dto : dtoList) {
            ProductIngredient pi = new ProductIngredient();
            pi.setQuantity(dto.getQuantity());

            // Зареждане на продукт и съставка от базата
            Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found: " + dto.getProductId()));
            pi.setProduct(product);

            Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId()).orElseThrow(() -> new RuntimeException("Ingredient not found: " + dto.getIngredientId()));
            pi.setIngredient(ingredient);

            productIngredientRepository.save(pi);
        }
    }
}
