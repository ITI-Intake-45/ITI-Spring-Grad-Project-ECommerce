package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.OrderDto;
import gov.iti.jet.ewd.ecom.exception.OrderNotFoundException;
import gov.iti.jet.ewd.ecom.service.OrderService;
import gov.iti.jet.ewd.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("{userId}")
    String placeOrder(HttpSession session, @PathVariable("userId") Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        return orderService.checkout(session, userId);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getOrders(
            @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<OrderDto> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(
            @PathVariable int userId,
            @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderDto> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        Optional<OrderDto> orderDto = orderService.getOrderById(orderId);
        return orderDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/{orderId}")
    public ResponseEntity<OrderDto> getOrderForUser(
            @PathVariable int userId,
            @PathVariable int orderId) {

        Optional<OrderDto> orderDto = orderService.getOrderForUser(userId, orderId);
        return orderDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok("Order cancelled successfully.");
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }








}
