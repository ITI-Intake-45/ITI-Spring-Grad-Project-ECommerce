package gov.iti.jet.ewd.ecom.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    public void sendPasswordResetEmail(String email, String resetToken);
}
