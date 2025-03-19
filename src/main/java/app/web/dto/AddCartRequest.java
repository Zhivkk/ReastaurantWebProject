package app.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddCartRequest {

    private Long product_id;

    private int quantity;
}
