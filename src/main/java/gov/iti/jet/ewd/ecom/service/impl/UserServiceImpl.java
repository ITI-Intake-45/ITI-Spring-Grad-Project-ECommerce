package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.*;
import gov.iti.jet.ewd.ecom.entity.Cart;
import gov.iti.jet.ewd.ecom.entity.User;
import gov.iti.jet.ewd.ecom.exception.EmailAlreadyExistsException;
import gov.iti.jet.ewd.ecom.exception.InvalidCredentialsException;
import gov.iti.jet.ewd.ecom.exception.UserNotFoundException;
import gov.iti.jet.ewd.ecom.mapper.UserMapper;
import gov.iti.jet.ewd.ecom.repository.UserRepository;
import gov.iti.jet.ewd.ecom.service.CartService;
import gov.iti.jet.ewd.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> otpExpirationTime = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_DURATION = 10 * 60 * 1000; // 10 minutes in milliseconds


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
//        this.emailServiceImpl = emailServiceImpl;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()) || userRepository.existsByphone(user.getPhone())) {

            throw new EmailAlreadyExistsException("Email or Phone already exist! Please try another email or phone number.");
        }
        // Hash the password
        String hashedPassword;
        hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
        user.setPassword(hashedPassword);

        // Initialize cart with bidirectional relationship
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public boolean phoneExists(String phone) {
        return userRepository.existsByphone(phone);
    }


    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public double changeBalance(CreditBalanceDto creditBalanceDto, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");

        if (!userRepository.existsByUserId(userDto.getUserId())) {
            throw new UserNotFoundException("User with ID '" +
                    userDto.getUserId() + "' doesn't exist");
        }
        return userRepository.updateCreditBalance(userDto.getUserId(), creditBalanceDto.getBalance());
    }

    @Override
    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException("email: " + email + " does not exist");
        }

        // Generate a 6-digit OTP
        String otpCode = String.format("%06d", new java.util.Random().nextInt(999999));

        // Store OTP with expiration time
        otpStorage.put(email, otpCode);
        otpExpirationTime.put(email, System.currentTimeMillis() + OTP_VALIDITY_DURATION);

        // Send OTP via email
        emailServiceImpl.sendOtpEmail(email, otpCode);
    }

    @Override
    public boolean verifyOtp(String email, String otpCode) {
        // Check if OTP exists for the email
        if (!otpStorage.containsKey(email)) {
            return false;
        }

        // Check if OTP has expired
        if (System.currentTimeMillis() > otpExpirationTime.get(email)) {
            // Remove expired OTP
            otpStorage.remove(email);
            otpExpirationTime.remove(email);
            return false;
        }

        // Verify OTP code
        boolean isValid = otpStorage.get(email).equals(otpCode);

        // If valid, keep the OTP for password reset step
        // If invalid, return false
        return isValid;
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        // Check if OTP verification was done
        if (!otpStorage.containsKey(email)) {
            throw new IllegalStateException("OTP verification not done or expired");
        }

        User user = getUserByEmail(email);

        // Hash the new password
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);

        // Save updated user
        userRepository.save(user);

        // Clear OTP data after successful password reset
        otpStorage.remove(email);
        otpExpirationTime.remove(email);
    }

    /**************** Updated login with manual authentication *************************/
    @Override
    public UserDto login(LoginRequestDto loginRequestDto, HttpSession session) {
        // Find user in database
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password"));

        // Verify password
        if (!BCrypt.checkpw(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        // Create Spring Security authentication manually
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null, // Don't store password
                        List.of((GrantedAuthority) () -> "ROLE_USER")
                );

        // Set the authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Store SecurityContext in HTTP session
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        // Create and store user DTO
        UserDto userDto = userMapper.toDTO(user);

        // Set user in session
        session.setAttribute("user", userDto);

        initializeSessionCart(session, userDto.getUserId());


        return userDto;
    }






    /**
     * Initialize session cart for logged-in user
     * - Load existing cart from database if available
     * - Create empty cart if no database cart exists
     */
    private void initializeSessionCart(HttpSession session, int userId) {
        try {
            // Try to load cart from database
            CartDTO cartDTO = cartService.loadCartFromDatabase(userId);

            // Set the cart in session with key "sessionCart"
            session.setAttribute("sessionCart", cartDTO);

            System.out.println("Session cart initialized for user: " + userId);
        } catch (Exception e) {
            // If error loading from database, create empty cart
            CartDTO emptyCart = cartService.createEmptyCart(userId);
            session.setAttribute("sessionCart", emptyCart);

            System.err.println("Error loading cart from database, created empty cart: " + e.getMessage());
        }
    }


    //update profile

    @Override
    public boolean updateProfile(UserDto user) {
        User existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));



        // Update allowed fields
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());

        userRepository.save(existingUser);

        return true;
    }
    @Override
    public void logout(HttpSession session) {

        // Clear Spring Security context
        SecurityContextHolder.clearContext();


        // Save cart to database before logout
        UserDto user = (UserDto) session.getAttribute("user");
        if (user != null) {
            try {
                cartService.saveSessionCartToDatabase(session, user.getUserId());
            } catch (Exception e) {
                // Log error but don't prevent logout
                System.err.println("Error saving cart on logout: " + e.getMessage());
            }
        }

        // Invalidate session
        session.invalidate();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        gov.iti.jet.ewd.ecom.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }


    @Override
    public void changePassword(int userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verify old password
        if (!BCrypt.checkpw(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect old password");
        }

        // Hash and update new password
        String hashedNewPassword = BCrypt.hashpw(changePasswordDto.getNewPassword(), BCrypt.gensalt(12));
        user.setPassword(hashedNewPassword);

        // Save updated user
        userRepository.save(user);
    }


}





