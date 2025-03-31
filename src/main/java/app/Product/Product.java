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
    private ProductCategory productCategory;

    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProductIngredient> productIngredients = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String preparation;

    private Double grammage;

    private BigDecimal price;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private ProductStatus productStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdOn;

    private Timestamp updatedOn;

}
