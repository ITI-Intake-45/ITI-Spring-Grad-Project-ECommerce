package gov.iti.jet.ewd.ecom.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean authenticateAdmin(String username, String password, HttpSession session) {
        // Check if credentials match admin
        // Note: In production, you should encode the stored password and compare
        if ("admin".equals(username) && "admin".equals(password)) {
            // Create Spring Security authentication for admin
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null, // Don't store password
                            List.of(new GrantedAuthority() {
                                @Override
                                public String getAuthority() {
                                    return "ROLE_ADMIN";
                                }
                            })
                    );

            // Set the authentication in SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Store SecurityContext in HTTP session
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            return true;
        }
        return false;
    }
}