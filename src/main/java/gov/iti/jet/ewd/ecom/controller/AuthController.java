//package gov.iti.jet.ewd.ecom.controller;
//
//import gov.iti.jet.ewd.ecom.dto.RegisterRequest;
//import gov.iti.jet.ewd.ecom.service.AuthService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class AuthController {
//
//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
//        authService.registerUser(request);
//        return ResponseEntity.ok("User registered successfully.");
//    }
//}
