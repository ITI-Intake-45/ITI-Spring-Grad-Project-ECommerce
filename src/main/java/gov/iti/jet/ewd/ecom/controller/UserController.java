package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.*;
import gov.iti.jet.ewd.ecom.entity.User;
import gov.iti.jet.ewd.ecom.exception.InvalidCredentialsException;
import gov.iti.jet.ewd.ecom.exception.UserNotFoundException;
import gov.iti.jet.ewd.ecom.mapper.UserMapper;
import gov.iti.jet.ewd.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDto) {
        User user = userMapper.createDtoToEntity(createUserDto);
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(userMapper.toDTO(savedUser), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PatchMapping("/change-balance")
    public ResponseEntity<UserDTO> updateBalance(
            @RequestBody CreditBalanceDTO creditBalanceDto,
            HttpSession session) {

        UserDTO userDto = (UserDTO) session.getAttribute("user");

        if (creditBalanceDto.getBalance() <= 0) {
            throw new IllegalArgumentException("Amount cannot be zero or less than zero!");
        }
        userService.changeBalance(creditBalanceDto, session);
        return ResponseEntity.ok(userMapper.toDTO(userService.getUserById(userDto.getUserId())));
    }

    /******************* User Login API ************************/
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDto,
                                         HttpSession session) {
        UserDTO userDto = userService.login(loginRequestDto, session);
        return ResponseEntity.ok(userDto);
    }

    /******************* User Logout API ************************/
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        userService.logout(session);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session, Authentication authentication) {
        // Check Spring Security authentication
        if (authentication != null && authentication.isAuthenticated()) {
            Object user = session.getAttribute("user");
            return ResponseEntity.ok(new SessionStatus(
                    true,
                    authentication.getName(),
                    authentication.getAuthorities().toString(),
                    user != null ? user.toString() : "No user in session"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SessionStatus(false, null, null, "No active session"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpSession session) {
        UserDTO userDto = (UserDTO) session.getAttribute("user");
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        try {
            // this.forgotPasswordDTO.setEmail(forgotPasswordDTO.getEmail());
            // userService.forgotPassword(forgotPasswordDTO.getEmail());
            String email = forgotPasswordDTO.getEmail();
            userService.forgotPassword(email);

            return ResponseEntity.ok("Password reset instructions sent to your email");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    /*
        @PostMapping("/verify-otp")
        public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO otpObj){

            boolean isValid = userService.verifyOtp(this.forgotPasswordDTO.getEmail(), otpObj.getOtp());
            if (isValid) {
                return ResponseEntity.ok("OTP verified successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
            }
        }
            */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO otpObj) {
        try {

            boolean isValid = userService.verifyOtp(otpObj.getEmail(), otpObj.getOtp());
            return isValid
                    ? ResponseEntity.ok("OTP verified successfully")
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    // Helper class for session status response
    public static class SessionStatus {
        public boolean authenticated;
        public String username;
        public String authorities;
        public String sessionData;

        public SessionStatus(boolean authenticated, String username, String authorities, String sessionData) {
            this.authenticated = authenticated;
            this.username = username;
            this.authorities = authorities;
            this.sessionData = sessionData;
        }

    }

    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateUserDTO updatedUser, HttpSession session) {
        UserDTO currentUser = (UserDTO) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        String currentEmail = userService.getUserById(currentUser.getUserId()).getEmail();
        String currentPhone = userService.getUserById(currentUser.getUserId()).getPhone();
        if (!currentEmail.equals(updatedUser.getEmail()) && userService.emailExists(updatedUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email already exists");
        }
        if (!currentPhone.equals(updatedUser.getPhone()) && userService.phoneExists(updatedUser.getPhone())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phone already exists");
        }
        currentUser.setName(updatedUser.getName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setAddress(updatedUser.getAddress());
        boolean success = userService.updateProfile(currentUser);
        if (success) {
            session.setAttribute("user", currentUser); // Update session
            return ResponseEntity.ok("Profile updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update profile");
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid
            @RequestBody
            ChangePasswordDTO changePasswordDto,
            Authentication authentication,
            HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UserDTO currentUser = (UserDTO) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            userService.changePassword(currentUser.getUserId(), changePasswordDto);
            return ResponseEntity.ok("Password changed successfully");
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}