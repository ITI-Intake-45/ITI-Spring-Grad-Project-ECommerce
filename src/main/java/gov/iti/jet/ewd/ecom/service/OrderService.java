package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.dto.OrderDto;
import gov.iti.jet.ewd.ecom.entity.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {

    String checkout(HttpSession session, int userId);
    Page<OrderDto> getAllOrders(Pageable pageable);
    Page<OrderDto> getOrdersByUserId(int userId, Pageable pageable);
    Optional<OrderDto> getOrderById(int orderId);
    Optional<OrderDto> getOrderForUser(int userId, int orderId);
    void cancelOrder(int orderId);

}
