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
public class Message {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @Column (nullable = false)
    private String subject;

    @Column (nullable = false)
    private String messageText;

    @Enumerated(EnumType.STRING)
    private MessageStaus messageStatus;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
