package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.dto.OrderDTO;
import gov.iti.jet.ewd.ecom.dto.OrdersListStatsDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {

    String checkout(HttpSession session, int userId);
    
    OrdersListStatsDTO getAllOrdersStats();
    
    Page<OrderDTO> getAllOrders(Pageable pageable);
    
    Page<OrderDTO> getOrdersByUserId(int userId, Pageable pageable);
    
    Optional<OrderDTO> getOrderById(int orderId);
    
    Optional<OrderDTO> getOrderForUser(int userId, int orderId);
    
    void acceptOrder(int orderId);
    
    void cancelOrder(int orderId);

}
