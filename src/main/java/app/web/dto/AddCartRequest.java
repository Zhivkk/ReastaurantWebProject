package app.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddCartRequest {

    private Long product_id;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;
}
