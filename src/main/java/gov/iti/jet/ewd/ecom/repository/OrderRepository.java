package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAllByOrderByOrderIdDesc(Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.items oi " +
            "LEFT JOIN FETCH oi.product p " +
            "LEFT JOIN FETCH p.category " +
            "ORDER BY o.orderId DESC")
    Page<Order> findAllOrders(Pageable pageable);

    Page<Order> findByUserUserId(int userId, Pageable pageable);

    Optional<Order> findByOrderId(int orderId);

    Optional<Order> findByOrderIdAndUserUserId(int orderId, int userId);





}
