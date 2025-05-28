package gov.iti.jet.ewd.ecom.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemDTO {

    private int cartItemId;
    private int productId;
    private int quantity;

    public CartItemDTO() {}

    public CartItemDTO(int cartItemId, int productId, int quantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.quantity = quantity;
    }

}
