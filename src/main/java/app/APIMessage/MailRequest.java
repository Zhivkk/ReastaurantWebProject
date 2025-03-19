package app.APIMessage;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailRequest {

    @NotBlank
    @Email
    private String recipient;// Имейл на получателя

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

}
