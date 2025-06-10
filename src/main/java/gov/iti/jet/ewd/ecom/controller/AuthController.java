package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.LoginRequestDTO;
import gov.iti.jet.ewd.ecom.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody LoginRequestDTO loginRequestDto,
                                             HttpSession session) {
        boolean authenticated = authService.authenticateAdmin(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                session
        );

        if (authenticated) {
            return ResponseEntity.ok("Admin logged in successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin credentials");
        }
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // Clear the SecurityContext
        SecurityContextHolder.clearContext();

        // Remove SecurityContext from session
        session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        // Invalidate the entire session (optional - use if you want to clear all session data)
        session.invalidate();

        return ResponseEntity.ok("Logged out successfully");
    }


    @GetMapping("/redirect")
    public ResponseEntity<String> redirectAfterLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.ok("redirect:/api/v1/users");
            }
            return ResponseEntity.ok("redirect:/api/v1/products");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    @GetMapping("/auth/status")
    public ResponseEntity<?> getAuthStatus(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(authentication.getAuthorities());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }
}