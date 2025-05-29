package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
