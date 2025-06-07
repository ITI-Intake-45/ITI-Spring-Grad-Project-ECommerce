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
    private ForgotPasswordDTO forgotPasswordDTO;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        User user = userMapper.createDtoToEntity(createUserDto);
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(userMapper.toDTO(savedUser), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PatchMapping("/change-balance")
    public ResponseEntity<UserDto> updateBalance(
            @RequestBody CreditBalanceDto creditBalanceDto,
            HttpSession session) {

        UserDto userDto = (UserDto) session.getAttribute("user");

        if (creditBalanceDto.getBalance() <= 0) {
            throw new IllegalArgumentException("Amount cannot be zero or less than zero!");
        }
        userService.changeBalance(creditBalanceDto, session);
        return ResponseEntity.ok(userMapper.toDTO(userService.getUserById(userDto.getUserId())));
    }

    /******************* User Login API ************************/
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto,
                                         HttpSession session) {
        UserDto userDto = userService.login(loginRequestDto, session);
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
public ResponseEntity<UserDto> getUserProfile(HttpSession session) {    
UserDto userDto = (UserDto) session.getAttribute("user");   
 return ResponseEntity.ok(userDto);
}

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        try {
            this.forgotPasswordDTO.setEmail(forgotPasswordDTO.getEmail());
            userService.forgotPassword(forgotPasswordDTO.getEmail());
            return ResponseEntity.ok("Password reset instructions sent to your email");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO otpObj){

        boolean isValid = userService.verifyOtp(this.forgotPasswordDTO.getEmail(), otpObj.getOtp());
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(this.forgotPasswordDTO.getEmail(),resetPasswordDTO.getPassword());
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
public ResponseEntity<String> updateProfile(@RequestBody UpdateUserDto updatedUser, HttpSession session) 
{    
UserDto currentUser = (UserDto) session.getAttribute("user");
    if (currentUser == null) {      
  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }    
String currentEmail = userService.getUserById(currentUser.getUserId()).getEmail();  
  String currentPhone = userService.getUserById(currentUser.getUserId()).getPhone();   
 if (currentEmail.equals(updatedUser.getEmail()) == false && userService.emailExists(updatedUser.getEmail())) {  
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email already exists");   
 } 
   if (currentPhone.equals(updatedUser.getPhone()) == false && userService.phoneExists(updatedUser.getPhone())) {      
  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phone already exists");   
 }    
currentUser.setName(updatedUser.getName());    
currentUser.setEmail(updatedUser.getEmail());    
currentUser.setPhone(updatedUser.getPhone());    
currentUser.setAddress(updatedUser.getAddress());    
boolean success = userService.updateProfile((UserDto) currentUser);    
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
            ChangePasswordDto changePasswordDto,
            Authentication authentication,
            HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        UserDto currentUser = (UserDto) session.getAttribute("user");
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