package gov.iti.jet.ewd.ecom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDTO {

    private String name;
    private String email;
    private String phone;
    private String address;
    public UpdateUserDTO(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
 
   
}