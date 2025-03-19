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
public class Errand { // описва поръчката

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user; // името на клиента (логнатия в системата)

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private ErrandStatus errandStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "errand")
    @OrderBy("createdOn DESC")
    private List<Cart> carts = new ArrayList<>();

    private BigDecimal price; // обща цена за поръчката

    private String addressForDelivery; // може да се раздели на отделни позиции – град, квартал, улица ....

    private int tableNumber; // номер на масата (когато се обслужва от сервитьор)

    private String note; //Коментар

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
