package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.OrderDto;
import gov.iti.jet.ewd.ecom.entity.*;
import gov.iti.jet.ewd.ecom.exception.OrderNotFoundException;
import gov.iti.jet.ewd.ecom.mapper.OrderMapper;
import gov.iti.jet.ewd.ecom.repository.CartRepository;
import gov.iti.jet.ewd.ecom.repository.OrderRepository;
import gov.iti.jet.ewd.ecom.service.CartService;
import gov.iti.jet.ewd.ecom.service.OrderService;
import gov.iti.jet.ewd.ecom.util.CostCalculator;
import gov.iti.jet.ewd.ecom.util.DataValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            OrderMapper orderMapper,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderMapper = orderMapper;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public String checkout(HttpSession session, int userId) {
        DataValidator.validateId(userId);

        Cart userCart = cartRepository.findByCartId(userId);
        if (userCart == null) {
            throw new IllegalArgumentException("Cart not found for user ID: " + userId);
        }
        User user = userCart.getUser();

        List<CartItem> cartItems = userCart.getItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }


        double totalCost = CostCalculator.calculateTotalCost(cartItems);

        if (totalCost > user.getCreditBalance()) {
            throw new IllegalStateException("Insufficient credit balance");
        }

        user.setCreditBalance(user.getCreditBalance() - totalCost);

        cartItems.forEach(item -> {
            Product product = item.getProduct();
            int stock = product.getStock();
            if (stock < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + item.getProduct().getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
        });

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalCost);
        order.setStatus(OrderStatus.PENDING);

        // Create OrderItems from CartItems
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setItemPrice(cartItem.getProduct().getPrice());

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart items from database
//        userCart.getItems().clear();
//        cartRepository.save(userCart);

        // Clear cart items from the session and also the database
        cartService.clearCartAfterCheckout(session, userId);


        return "Order #" + savedOrder.getOrderId() + " created successfully";
    }

    public Page<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllOrders(pageable);

        return orderPage.map(orderMapper::toDto);
    }


    public Page<OrderDto> getOrdersByUserId(int userId, Pageable pageable) {
        return orderRepository.findByUserUserId(userId, pageable)
                .map(orderMapper::toDto);
    }

    public Optional<OrderDto> getOrderById(int orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(orderMapper::toDto);
    }

    public Optional<OrderDto> getOrderForUser(int userId, int orderId) {
        return orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .map(orderMapper::toDto);
    }

    @Transactional
    public void cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }




}
