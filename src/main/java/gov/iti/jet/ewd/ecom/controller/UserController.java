
package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.LoginRequestDto;
import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.dto.CreateUserDto;
import gov.iti.jet.ewd.ecom.entity.User;
import gov.iti.jet.ewd.ecom.mapper.UserMapper;
import gov.iti.jet.ewd.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService , UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        User user = userMapper.createDtoToEntity(createUserDto);
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(userMapper.toDTO(savedUser),HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {

        User user =  userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PutMapping("/changeBalance")
    public ResponseEntity<UserDto> updateBalance(
            @RequestParam @Min(1) int userId,
            @RequestParam double amount) {

        if (amount == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }
        userService.changeBalance(userId, amount);
        return ResponseEntity.ok(userMapper.toDTO(userService.getUserById(userId)));
    }

    /******************* user Login Api ************************/

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpSession session) {
        UserDto userDto = userService.login(loginRequestDto, session);
        return ResponseEntity.ok(userDto);
    }

    /******************* user logout Api ************************/
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        userService.logout(session);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok("Session is active. User: " + user.toString());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session.");
        }
    }





}