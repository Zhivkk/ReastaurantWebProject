package app.Message;

import app.User.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message { //// изпращане на съобщение от потребителя до системата и отговор

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user; // името на клиента (логнатия в системата)

    @Column (nullable = false)
    private String subject;

    @Column (nullable = false)
    private String messageText;

    @Enumerated(EnumType.STRING)
    private MessageStaus messageStatus; //written, read, replied, deleted

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
