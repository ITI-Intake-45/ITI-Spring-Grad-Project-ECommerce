// I convert this class to be an interface that uses @Mapping
// we use @Mapping for better readability

package gov.iti.jet.ewd.ecom.mapper;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.dto.CartItemDTO;
import gov.iti.jet.ewd.ecom.entity.Cart;
import gov.iti.jet.ewd.ecom.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "cartId", source = "cartId")
    @Mapping(target = "cartItems", source = "items")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDTO toDTO(Cart cart);

    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "image", source = "product.image")
    CartItemDTO toDTO(CartItem cartItem);

}


//==================================================

//package gov.iti.jet.ewd.ecom.mapper;
//
//import gov.iti.jet.ewd.ecom.dto.CartDTO;
//import gov.iti.jet.ewd.ecom.dto.CartItemDTO;
//import gov.iti.jet.ewd.ecom.entity.Cart;
//import gov.iti.jet.ewd.ecom.entity.CartItem;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class CartMapper {
//
//    public CartDTO entityToDTO(Cart entity) {
//        CartDTO dto = new CartDTO();
//
//        dto.setUserId(entity.getUser().getUserId());
//        dto.setCartId(entity.getCartId());
//
//        List<CartItem> entityItems = entity.getItems();
//        List<CartItemDTO> dtoItems = new ArrayList<>();
//
//        entityItems.forEach(item -> {
//            CartItemDTO itemDto = new CartItemDTO();
//            itemDto.setCartItemId(item.getId());
//            itemDto.setProductId(item.getProduct().getProductId());
//            itemDto.setQuantity(item.getQuantity());
//            dtoItems.add(itemDto);
//        });
//
//        dto.setCartItems(dtoItems);
//        return dto;
//    }
//}
