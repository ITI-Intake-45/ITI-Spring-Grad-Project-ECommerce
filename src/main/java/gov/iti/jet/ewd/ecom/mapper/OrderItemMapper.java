package gov.iti.jet.ewd.ecom.mapper;

import gov.iti.jet.ewd.ecom.dto.OrderItemDto;
import gov.iti.jet.ewd.ecom.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {


    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.image", target = "imageUrl")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(source = "productId", target = "product.productId")
    @Mapping(source = "productName", target = "product.name")
    @Mapping(source = "imageUrl", target = "product.image")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto orderItemDto);
}