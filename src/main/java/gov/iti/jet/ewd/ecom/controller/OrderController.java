package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.OrderDTO;
import gov.iti.jet.ewd.ecom.dto.OrdersListStatsDTO;
import gov.iti.jet.ewd.ecom.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@SuppressWarnings("unused")
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
    ResponseEntity<Page<OrderDTO>> getOrders(
            @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<OrderDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/stats")
    ResponseEntity<OrdersListStatsDTO> getAllOrdersStats() {
        OrdersListStatsDTO allOrdersList = orderService.getAllOrdersStats();
        return ResponseEntity.ok(allOrdersList);
    }

    @GetMapping("/user/{userId}")
    ResponseEntity<?> getOrdersByUserId(
            @PathVariable int userId,
            @PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        Optional<OrderDTO> orderDto = orderService.getOrderById(orderId);
        return orderDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/{orderId}")
    ResponseEntity<OrderDTO> getOrderForUser(
            @PathVariable int userId,
            @PathVariable int orderId) {

        Optional<OrderDTO> orderDto = orderService.getOrderForUser(userId, orderId);
        return orderDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{orderId}/accept")
    ResponseEntity<String> acceptOrder(@PathVariable int orderId) {
        orderService.acceptOrder(orderId);
        return ResponseEntity.ok("Order accepted successfully.");
    }

    @PatchMapping("/{orderId}/cancel")
    ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");

    }

}
