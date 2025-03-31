package app.Cart;

import app.Errand.Errand;
import app.Product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Errand errand;

    @ManyToOne
    private Product product;

    @Column (nullable = false)
    private int quantity;

    private Boolean isReady;

    private Double minProductQuantity;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
