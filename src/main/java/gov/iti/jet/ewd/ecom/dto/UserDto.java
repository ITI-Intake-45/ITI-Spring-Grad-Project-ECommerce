package gov.iti.jet.ewd.ecom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private int userId;
    private String name;
    private String email;
    private String address;
    private String phone;
    private double creditBalance;

}