package gov.iti.jet.ewd.ecom.dto;

import gov.iti.jet.ewd.ecom.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private int orderId;
    private int userId;
    private String email;
    private OrderStatus status;
    private double totalPrice;
    private List<OrderItemDto> items;
}
