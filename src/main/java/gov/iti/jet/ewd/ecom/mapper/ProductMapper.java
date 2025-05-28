package gov.iti.jet.ewd.ecom.mapper;

import gov.iti.jet.ewd.ecom.dto.ProductDTO;
import gov.iti.jet.ewd.ecom.entity.Product;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    ProductDTO entityToDTO(Product entity);

    Product dtoToEntity(ProductDTO dto);
}
