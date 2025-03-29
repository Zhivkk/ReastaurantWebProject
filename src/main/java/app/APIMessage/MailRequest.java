package app.APIMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {

    @NotBlank
    @Email
    private String recipient;// Имейл на получателя

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

}
