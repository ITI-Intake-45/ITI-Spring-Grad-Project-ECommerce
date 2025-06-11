package gov.iti.jet.ewd.ecom.dto;

import lombok.*;

@Setter
@Getter
public class CartItemDTO {

    private int cartItemId;
    private int productId;
    private int quantity;
    private String productName;
    private double price;
    private String image;

    public CartItemDTO() {}

    public CartItemDTO(int cartItemId, int productId, int quantity, String productName, double price, String image) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
        this.image = image;
    }


    // Constructor for session cart items (without cartItemId)
    public CartItemDTO(int productId, String productName, double price, int quantity, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = imageUrl;
        this.cartItemId = 0; // 0 indicates it's not saved to database yet
    }

}
