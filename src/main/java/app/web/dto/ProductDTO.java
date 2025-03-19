package app.web.dto;

import app.Data.ProductIngredientDTO;
import app.Product.ProductCategory;
import app.Product.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String productName;
    private ProductCategory productCategory;
    private String description;
    private String preparation;
    private Double grammage;
    private BigDecimal price;
    private String picture;
    private ProductStatus productStatus;
    private List<ProductIngredientDTO> productIngredients;
}

