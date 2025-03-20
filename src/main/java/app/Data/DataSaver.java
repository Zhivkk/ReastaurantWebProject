package app.Data;

import app.Ingredient.Ingredient;
import app.Ingredient.IngredientRepository;
import app.Product.Product;
import app.Product.ProductRepository;
import app.ProductIngredient.ProductIngredient;
import app.ProductIngredient.ProductIngredientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class DataSaver {

    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final ObjectMapper objectMapper;

    public DataSaver(IngredientRepository ingredientRepository, ProductRepository productRepository,
                     ProductIngredientRepository productIngredientRepository, ObjectMapper objectMapper) {
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.objectMapper = objectMapper;
    }
    //Сваля данните от JSON файловете на GitHub и ги зарежда в базата данни
    @PreDestroy
    public void saveDataBeforeShutdown() throws IOException, InterruptedException {
        saveIngredients();
        saveProducts();
        saveProductIngredients();
        commitAndPushToGitHub();
        System.out.println("Данните са записани успешно в JSON файловете.");
    }

    private void saveIngredients() throws IOException {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        objectMapper.writeValue(new File("ingredients.json"), ingredients);
    }

    private void saveProducts() throws IOException {
        List<Product> products = productRepository.findAll();
        objectMapper.writeValue(new File("products.json"), products);
    }

    private void saveProductIngredients() throws IOException {
        List<ProductIngredient> productIngredients = productIngredientRepository.findAll();
        objectMapper.writeValue(new File("product_ingredients.json"), productIngredients);
    }

    //Записва данните в JSON файловете на GitHub
    public void commitAndPushToGitHub() throws IOException, InterruptedException {
        String repoUrl = "https://github.com/Zhivkk/RestaurantWebProjectResources.git";
        String githubToken = "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"; // Пази го сигурно, можеш да го сложиш в application.properties
        String commitMessage = "Auto-update JSON files";

        // Записваме bash скрипт за commit и push
        String script = """
        git config --global user.email "your-email@example.com"
        git config --global user.name "Your Name"
        git clone https://%s@github.com/ТВОЙ_ПРОФИЛ/ТВОЕ_РЕПО.git repo
        cd repo
        cp ../ingredients.json .
        cp ../products.json .
        cp ../product_ingredients.json .
        git add .
        git commit -m "%s"
        git push origin main
        """.formatted(githubToken, commitMessage);

        // Записваме скрипта във файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("update_github.sh"))) {
            writer.write(script);
        }

        // Изпълняваме скрипта
        Process process = Runtime.getRuntime().exec("bash update_github.sh");
        process.waitFor();
    }
}

