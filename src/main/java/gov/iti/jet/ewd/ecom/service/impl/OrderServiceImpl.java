package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.dto.CartItemDTO;
import gov.iti.jet.ewd.ecom.dto.OrderDTO;
import gov.iti.jet.ewd.ecom.dto.OrdersListStatsDTO;
import gov.iti.jet.ewd.ecom.entity.*;
import gov.iti.jet.ewd.ecom.exception.OrderNotFoundException;
import gov.iti.jet.ewd.ecom.mapper.OrderMapper;
import gov.iti.jet.ewd.ecom.repository.CartRepository;
import gov.iti.jet.ewd.ecom.repository.OrderRepository;
import gov.iti.jet.ewd.ecom.repository.ProductRepository;
import gov.iti.jet.ewd.ecom.repository.UserRepository;
import gov.iti.jet.ewd.ecom.service.CartService;
import gov.iti.jet.ewd.ecom.service.OrderService;
import gov.iti.jet.ewd.ecom.util.CostCalculator;
import gov.iti.jet.ewd.ecom.util.DataValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
    Here I made use of the fact that the cart id is just a reference to the user id.

    The checkout function is transactional, so if there is no stock available, then the
    subtracted balance should return to the user account automatically.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
        public OrderServiceImpl(OrderRepository orderRepository,
                                CartRepository cartRepository,
                                OrderMapper orderMapper,
                                CartService cartService,
                                UserRepository userRepository,
                                ProductRepository productRepository) {
            this.orderRepository = orderRepository;
            this.cartRepository = cartRepository;
            this.orderMapper = orderMapper;
            this.cartService = cartService;
            this.userRepository = userRepository;
            this.productRepository = productRepository;
        }
    
        @Override
    @Transactional
    public String checkout(HttpSession session, int userId) {
        DataValidator.validateId(userId);
        
        // Validate user is logged in
        cartService.validateUserSession(session);
        
        // Get session cart instead of database cart
        CartDTO sessionCart = cartService.getSessionCart(session);
        if (sessionCart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }
        
        // Get user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + userId));
        
        // Calculate total cost from session cart
        double totalCost = sessionCart.getTotalPrice();
        
        // Check user's credit balance
        if (totalCost > user.getCreditBalance()) {
            throw new IllegalStateException("Insufficient credit balance. Required: " + totalCost + 
                    ", Available: " + user.getCreditBalance());
        }
        
        // Validate stock and prepare for order creation
        List<Product> productsToUpdate = new ArrayList<>();
        for (CartItemDTO cartItem : sessionCart.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
        
        // Check stock availability
        if (product.getStock() < cartItem.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStock() + ", Required: " + cartItem.getQuantity());
        }
        
        productsToUpdate.add(product);
    }
    
    // All validations passed - proceed with checkout
    
    // Deduct credit balance
    user.setCreditBalance(user.getCreditBalance() - totalCost);
    userRepository.save(user);
    
    // Update product stock
     for (int i = 0; i < sessionCart.getCartItems().size(); i++) {
        CartItemDTO cartItem = sessionCart.getCartItems().get(i);
        Product product = productsToUpdate.get(i);
        product.setStock(product.getStock() - cartItem.getQuantity());
        productRepository.save(product);
    }
    
    // Create order
    Order order = new Order();
    order.setUser(user);
    order.setTotalPrice(totalCost);
    order.setStatus(OrderStatus.PENDING);
    
    // Create OrderItems from session cart items
    for (CartItemDTO cartItem : sessionCart.getCartItems()) {
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setItemPrice(cartItem.getPrice()); // Use price from cart item
        
        order.getItems().add(orderItem);
    }
    
    Order savedOrder = orderRepository.save(order);
    
    // Clear both session cart and database cart after successful checkout
    cartService.clearCartAfterCheckout(session, userId);
    
    return "Order #" + savedOrder.getOrderId() + " created successfully";
}
    @Override
    public OrdersListStatsDTO getAllOrdersStats() {
        List<Order> allOrders = orderRepository.findAll();
        
        OrdersListStatsDTO statsDTO = new OrdersListStatsDTO();
        
        statsDTO.totalNumberOfOrders = allOrders.size();
        for (Order order: allOrders) {
            switch (order.getStatus()) {
                case OrderStatus.PENDING -> {
                    statsDTO.pendingOrdersCount++;
                }
                case OrderStatus.ACCEPTED -> {
                    statsDTO.acceptedOrdersCount++;
                }
                case OrderStatus.CANCELLED -> {
                    statsDTO.cancelledOrdersCount++;
                }
            }
        }
        
        return statsDTO;
    }

    @Override
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllOrders(pageable);

        return orderPage.map(orderMapper::toDto);
    }


    @Override
    public Page<OrderDTO> getOrdersByUserId(int userId, Pageable pageable) {
        return orderRepository.findByUserUserId(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Optional<OrderDTO> getOrderById(int orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(orderMapper::toDto);
    }

    @Override
    public Optional<OrderDTO> getOrderForUser(int userId, int orderId) {
        return orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .map(orderMapper::toDto);
    }
    
    @Transactional
    @Override
    public void acceptOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.ACCEPTED) {
            throw new IllegalStateException("Order is already accepted");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        userRepository.updateCreditBalance(order.getUser().getUserId(), order.getTotalPrice() + order.getUser().getCreditBalance());
    }

}
