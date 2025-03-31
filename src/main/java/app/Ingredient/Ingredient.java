package app.Ingredient;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (nullable = false)
    private String name;

    private Double quantity;

    private Double minQuantity;

    @CreationTimestamp
    @Column(updatable = false)
    Timestamp createdOn;

    Timestamp updatedOn;

}
