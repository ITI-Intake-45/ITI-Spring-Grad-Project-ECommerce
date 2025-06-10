package gov.iti.jet.ewd.ecom.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetDTO {

    private String token;

    @Size(min = 4, max = 10, message = "Password must be between 4 and 10 characters")
    private String newPassword;
}