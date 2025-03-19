package app.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductIngredientDTO {

        @JsonProperty("product_id")
        private Long productId;

        @JsonProperty("ingredient_id")
        private Long ingredientId;

        private Double quantity;
}
