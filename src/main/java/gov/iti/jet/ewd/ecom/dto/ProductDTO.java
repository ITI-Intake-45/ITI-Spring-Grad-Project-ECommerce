package gov.iti.jet.ewd.ecom.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDTO {

    private int productId;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String image;
    private int stock;
    private String status;

    public ProductDTO() {}
}
