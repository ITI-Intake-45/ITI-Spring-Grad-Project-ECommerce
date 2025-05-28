package gov.iti.jet.ewd.ecom.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CartDTO {

    private int userId;
    private int cartId;
    private List<CartItemDTO> cartItems;

    public CartDTO() {}

    public CartDTO(int userId, int cartId, List<CartItemDTO> cartItems) {
        this.userId = userId;
        this.cartId = cartId;
        this.cartItems = cartItems;
    }

}
