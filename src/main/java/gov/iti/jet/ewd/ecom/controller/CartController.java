package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.AddToCartRequest;
import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public CartDTO getCartForUser(@PathVariable("cartId") Integer cartId) {

        if (cartId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return cartService.getUserCart(cartId);
    }


    // Get the cart from the session.
    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        try {
            CartDTO cart = cartService.getSessionCart(session);
            return ResponseEntity.ok(cart);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpSession session) {

        try {
            CartDTO cart = cartService.addToSessionCart(
                    session,
                    request.getProductId(),
                    request.getQuantity());

            return ResponseEntity.ok(cart);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("logged in")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication required: " + e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Stock error: " + e.getMessage());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable int productId,
            HttpSession session) {

        try {
            CartDTO cart = cartService.removeFromSessionCart(session, productId);
            return ResponseEntity.ok(cart);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required: " + e.getMessage());
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable int productId,
            @RequestParam int quantity,
            HttpSession session) {

        try {
            CartDTO cart = cartService.updateSessionCartItemQuantity(session, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("logged in")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authentication required: " + e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Stock error: " + e.getMessage());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }


}
