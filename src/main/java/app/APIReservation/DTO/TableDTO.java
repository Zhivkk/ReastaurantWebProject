package app.APIReservation.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TableDTO {
    private Long id;
    private Long tableId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private LocalDate date;
    private int guests;
    private String message;
}

