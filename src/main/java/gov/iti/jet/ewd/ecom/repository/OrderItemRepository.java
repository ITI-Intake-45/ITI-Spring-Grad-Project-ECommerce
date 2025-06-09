package gov.iti.jet.ewd.ecom.repository;

import gov.iti.jet.ewd.ecom.entity.OrderItem;
import gov.iti.jet.ewd.ecom.entity.Product;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderItemRepository extends Repository<OrderItem, Integer> {

    List<OrderItem> findAllByProduct(Product product);
}
