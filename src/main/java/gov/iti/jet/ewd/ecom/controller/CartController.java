package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.service.CartService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
