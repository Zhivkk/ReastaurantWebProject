package app.web.dto;

import app.User.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequestAdmin {

    private UUID id;

    @Size(max = 20, message = "First name can't have more than 20 symbols")
    private String firstName;

    @Size(max = 20, message = "Last name can't have more than 20 symbols")
    private String lastName;

    @Email(message = "Requires correct email format")
    private String email;

    @URL(message = "Requires correct web link format")
    private String profilePicture;

    @Size(min = 10, max = 10, message = "The phone number must be 10 digits long")
    private String phone;

    private String address;

    private BigDecimal accountAmount;

    private UserRole role;

}
