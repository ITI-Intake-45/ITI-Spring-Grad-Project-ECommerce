package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import jakarta.servlet.http.HttpSession;

public interface CartService {

    CartDTO getUserCart(int cartId);

    
    // Session-based cart operations
    CartDTO getSessionCart(HttpSession session);
    public CartDTO addToSessionCart(HttpSession session, int productId, int quantity);
    public CartDTO removeFromSessionCart(HttpSession session, int productId);
    public CartDTO updateSessionCartItemQuantity(HttpSession session, int productId, int quantity);
    public void clearSessionCart(HttpSession session);


    // Database operations
    public void saveSessionCartToDatabase(HttpSession session, int userId);
    public CartDTO loadCartFromDatabase(int userId);


    // Checkout operation - clears cart after successful order
    public void clearCartAfterCheckout(HttpSession session, int userId);

    // Helper methods
    public CartDTO createEmptyCart(int userId);

    // Authentication helper
    public void validateUserSession(HttpSession session);

}
