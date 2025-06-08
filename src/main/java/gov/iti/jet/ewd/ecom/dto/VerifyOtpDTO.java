package gov.iti.jet.ewd.ecom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyOtpDTO {
    String email;
    String otp;
}
