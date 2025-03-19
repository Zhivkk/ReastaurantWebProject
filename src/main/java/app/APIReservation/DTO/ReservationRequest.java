package app.APIReservation.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {

    @NotBlank(message = "User name is required")
    private String userName;

    @NotBlank(message = "User phone is required")
    private String userPhone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "User email is required")
    private String userEmail;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Min(value = 1, message = "Guests must be at least 1")
    private int guests;

    private String message;
}
