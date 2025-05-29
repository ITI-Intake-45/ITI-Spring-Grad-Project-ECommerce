package gov.iti.jet.ewd.ecom.mapper;

import gov.iti.jet.ewd.ecom.dto.OrderDto;
import gov.iti.jet.ewd.ecom.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.email", target = "email")
    OrderDto toDto(Order order);

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "email", target = "user.email")
    @Mapping(target = "user.password", ignore = true) // Sensitive data, not in DTO
    @Mapping(target = "user.name", ignore = true) // Not present in OrderDto
    @Mapping(target = "user.orders", ignore = true) // Circular reference, managed separately
    Order toEntity(OrderDto orderDto);
}
