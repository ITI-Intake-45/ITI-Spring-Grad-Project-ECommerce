package gov.iti.jet.ewd.ecom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
    @NotNull
    private Integer productId;

    @Min(1)
    private int quantity = 1;
}
