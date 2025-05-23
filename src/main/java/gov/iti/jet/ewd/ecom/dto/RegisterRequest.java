package gov.iti.jet.ewd.ecom.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    // add the remaining fields

}
