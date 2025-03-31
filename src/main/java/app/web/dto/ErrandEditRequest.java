package app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrandEditRequest {

    @Size(max = 50, message = "Address for delivery can't have more than 50 symbols")
    @NotNull(message = "Address for delivery is required")
    private String addressForDelivery;

}
