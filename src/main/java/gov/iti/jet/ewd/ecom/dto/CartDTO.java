package gov.iti.jet.ewd.ecom.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CartDTO {

    private int userId;
    private int cartId;
    private List<CartItemDTO> cartItems = new ArrayList<>();
    private double totalPrice;

    public CartDTO() {}

    public CartDTO(int userId, int cartId, List<CartItemDTO> cartItems, double totalPrice) {
        this.userId = userId;
        this.cartId = cartId;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
    }


    public void addItem(CartItemDTO item) {
        // Check if product already exists in cart
        CartItemDTO existingItem = cartItems.stream()
                .filter(i -> i.getProductId() == item.getProductId())
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            cartItems.add(item);
        }
        calculateTotalPrice();
    }

    public void removeItem(int productId) {
        cartItems.removeIf(item -> item.getProductId() == productId);
        calculateTotalPrice();
    }

    public void updateItemQuantity(int productId, int quantity) {
        cartItems.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        this.totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

}
