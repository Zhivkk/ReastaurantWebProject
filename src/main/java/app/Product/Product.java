package app.Product;

import app.ProductIngredient.ProductIngredient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private ProductCategory productCategory; // soup, salad, appetizer, main course, dessert, soft drink, alcohol, others (енумерация)

    private String description; // Кратко описание на продукта

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductIngredient> productIngredients = new ArrayList<>();// съдържа рецептата за продукта (съставка/количество)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preparation; // начин на приготвяне;

    private Double grammage; // количество на една порция

    private BigDecimal price;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private ProductStatus productStatus; // available, out of stock,    (енумерация)

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdOn;

    private Timestamp updatedOn;

}
