package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.CartDTO;
import gov.iti.jet.ewd.ecom.dto.LoginRequestDto;
import gov.iti.jet.ewd.ecom.dto.UserDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailServiceImpl emailServiceImpl;

    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> otpExpirationTime = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_DURATION = 10 * 60 * 1000; // 10 minutes in milliseconds


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailServiceImpl emailServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("User with email '" +
                    user.getEmail() + "' already exists");
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
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public int changeBalance(int userId, double amount) {
        if (!userRepository.existsByUserId(userId)) {
            throw new UserNotFoundException("User with ID '" +
                    userId + "' doesn't exist");
        }
        return userRepository.updateCreditBalance(userId, amount);
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

    /****************login   /  logout *************************/
    @Override
    public UserDto login(LoginRequestDto loginRequestDto, HttpSession session) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password"));

        if (!BCrypt.checkpw(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        UserDto userDto = userMapper.toDTO(user);

        // Set user in session
        session.setAttribute("user", userDto);


        // Initialize session cart for the logged-in user
        initializeSessionCart(session, userDto.getUserId());

        return userDto;
    }



    @Override
    public void logout(HttpSession session) {
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

}
