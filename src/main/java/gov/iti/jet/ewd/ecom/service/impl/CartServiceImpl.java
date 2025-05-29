package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.dto.CartItemDTO;
import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.entity.Cart;
import gov.iti.jet.ewd.ecom.entity.CartItem;
import gov.iti.jet.ewd.ecom.entity.Product;
import gov.iti.jet.ewd.ecom.entity.User;
import gov.iti.jet.ewd.ecom.mapper.CartMapper;
import gov.iti.jet.ewd.ecom.repository.CartRepository;
import gov.iti.jet.ewd.ecom.repository.ProductRepository;
import gov.iti.jet.ewd.ecom.repository.UserRepository;
import gov.iti.jet.ewd.ecom.service.CartService;
import gov.iti.jet.ewd.ecom.util.DataValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "sessionCart";

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartMapper cartMapper;


    @Override
    public CartDTO getUserCart(int cartId) {
        DataValidator.validateId(cartId);

        Cart entity = cartRepository.findByCartId(cartId);
        if (entity == null) {
            throw new ResourceNotFoundException("Cart not found for ID: " + cartId);
        }

//        return cartMapper.entityToDTO(entity);
        return cartMapper.toDTO(entity);
    }


    @Override
    public CartDTO getSessionCart(HttpSession session) {
        CartDTO cart = (CartDTO) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            Integer userId = getUserIdFromSession(session);
            cart = createEmptyCart(userId != null ? userId : 0);
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public CartDTO addToSessionCart(HttpSession session, int productId, int quantity) {
        CartDTO cart = getSessionCart(session);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create CartItemDTO without cartItemId (session cart)
        CartItemDTO itemDTO = new CartItemDTO(
                productId,
                product.getName(),
                product.getPrice(),
                quantity,
                product.getImage()
        );

        cart.addItem(itemDTO);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    public CartDTO removeFromSessionCart(HttpSession session, int productId) {
        CartDTO cart = getSessionCart(session);
        cart.removeItem(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    public CartDTO updateSessionCartItemQuantity(HttpSession session, int productId, int quantity) {
        CartDTO cart = getSessionCart(session);
        if (quantity <= 0) {
            cart.removeItem(productId);
        } else {
            cart.updateItemQuantity(productId, quantity);
        }
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    @Override
    public void clearSessionCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    @Override
    @Modifying
    @Transactional
    public void saveSessionCartToDatabase(HttpSession session, int userId) {
        CartDTO sessionCart = getSessionCart(session);

        System.out.println("=== SAVE CART DEBUG ===");
        System.out.println("User ID: " + userId);
        System.out.println("Session cart items: " + sessionCart.getCartItems().size());

        if (sessionCart.getCartItems().isEmpty()) {
            System.out.println("No items to save - clearing existing cart");
            // Clear existing cart items if session cart is empty
            Cart existingCart = cartRepository.findById(userId).orElse(null);
            if (existingCart != null) {
                existingCart.getItems().clear();
                cartRepository.save(existingCart);
            }
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find existing cart or use the one created during user registration
        Cart cart = cartRepository.findById(userId).orElse(null);

        if (cart == null) {
            // This should not happen since cart is created during user registration
            // But just in case, create a new one
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
            System.out.println("Created new cart for user: " + userId);
        } else {
            // Clear existing items before adding new ones
            cart.getItems().clear();
            System.out.println("Found existing cart, cleared items");
        }

        // Add session cart items to database cart
        for (CartItemDTO itemDTO : sessionCart.getCartItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(itemDTO.getQuantity());
            cartItem.setCart(cart);

            cart.addItem(cartItem);
        }

        cartRepository.save(cart);
        System.out.println("Cart saved successfully with " + cart.getItems().size() + " items");

        // Clear session cart after saving
        clearSessionCart(session);
    }

    @Override
    public CartDTO loadCartFromDatabase(int userId) {
        Cart cart = cartRepository.findById(userId).orElse(null);
        if (cart != null) {
            return cartMapper.toDTO(cart);
        }
        return createEmptyCart(userId);
    }

    @Override
    public CartDTO createEmptyCart(int userId) {
        CartDTO cart = new CartDTO();
        cart.setUserId(userId);
        cart.setCartId(0); // 0 indicates session cart, not saved to DB yet
        return cart;
    }

    private Integer getUserIdFromSession(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDto) {
            return ((UserDto) userObj).getUserId();
        }
        return null;
    }
}