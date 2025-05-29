package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.AddToCartRequest;
import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<CartDTO> getCart(HttpSession session) {
        CartDTO cart = cartService.getSessionCart(session);
        return ResponseEntity.ok(cart);
    }


    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpSession session) {

        CartDTO cart = cartService.addToSessionCart(
                session,
                request.getProductId(),
                request.getQuantity());

        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(
            @PathVariable int productId,
            HttpSession session) {

        CartDTO cart = cartService.removeFromSessionCart(session, productId);
        return ResponseEntity.ok(cart);
    }


    @PutMapping("/update/{productId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable int productId,
            @RequestParam int quantity,
            HttpSession session) {

        CartDTO cart = cartService.updateSessionCartItemQuantity(session, productId, quantity);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        cartService.clearSessionCart(session);
        return ResponseEntity.ok("Cart cleared successfully.");
    }


//    @PostMapping("/save-to-database")
//    public ResponseEntity<String> saveCartToDatabase(HttpSession session) {
//        UserDto user = (UserDto) session.getAttribute("user");
//        if (user == null) {
//            return ResponseEntity.badRequest().body("User is not logged in.");
//        }
//
//        cartService.saveSessionCartToDatabase(session, user.getUserId());
//        return ResponseEntity.ok("Cart saved to database successfully.");
//    }


}
