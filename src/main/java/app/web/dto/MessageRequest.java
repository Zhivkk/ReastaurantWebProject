package app.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {

    private String name;

    private String email;

    private String subject;

    private String message;
}
