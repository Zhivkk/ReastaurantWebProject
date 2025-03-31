package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {

    @Size(min = 6, message = "Username must be at least 6 symbols")
    private String username;

    @Size(min = 10, max = 10, message = "The phone number must be 10 digits long")
    private String phone;

    @Email(message = "Requires correct email format")
    private String email;

    private String address;

    private String firstName;

    private String lastName;

    private String profilePicture;

}
