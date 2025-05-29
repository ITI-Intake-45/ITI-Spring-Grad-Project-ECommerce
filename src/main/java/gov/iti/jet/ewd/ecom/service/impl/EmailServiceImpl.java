package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    // Get sender email from properties
    @Value("${spring.mail.username:noreply@ecommerce.com}")
    private String fromEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String email, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset OTP Code");
            message.setText("Dear User,\n\nYour OTP code for password reset is: " + otpCode +
                    "\n\nThis code will expire in 10 minutes. If you did not request a password reset, please ignore this email.\n\nBest regards,\nE-commerce Team");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}