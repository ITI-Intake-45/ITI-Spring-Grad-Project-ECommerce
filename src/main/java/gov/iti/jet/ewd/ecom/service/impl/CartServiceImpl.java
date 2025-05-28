package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.entity.Cart;
import gov.iti.jet.ewd.ecom.mapper.CartMapper;
import gov.iti.jet.ewd.ecom.repository.CartRepository;
import gov.iti.jet.ewd.ecom.service.CartService;
import gov.iti.jet.ewd.ecom.util.DataValidator;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartDTO getUserCart(int cartId) {
        DataValidator.validateId(cartId);

        Cart entity = cartRepository.findByCartId(cartId);
        if (entity == null) {
            throw new ResourceNotFoundException("Cart not found for ID: " + cartId);
        }

        return cartMapper.entityToDTO(entity);
    }
}
