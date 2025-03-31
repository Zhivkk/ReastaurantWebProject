package app.Errand;

import app.Cart.Cart;
import app.User.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Errand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private ErrandStatus errandStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "errand")
    @OrderBy("createdOn DESC")
    private List<Cart> carts = new ArrayList<>();

    private BigDecimal price;

    private String addressForDelivery;

    private int tableNumber;

    private String note;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
