package gov.iti.jet.ewd.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private int productId;
    private String productName;
    private double itemPrice;
    private int quantity;
    private String imageUrl;
}
