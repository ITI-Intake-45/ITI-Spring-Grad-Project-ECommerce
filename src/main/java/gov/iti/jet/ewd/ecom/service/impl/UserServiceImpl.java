package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.dto.LoginRequestDto;
import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.entity.Cart;
import gov.iti.jet.ewd.ecom.entity.User;
import gov.iti.jet.ewd.ecom.exception.EmailAlreadyExistsException;
import gov.iti.jet.ewd.ecom.exception.InvalidCredentialsException;
import gov.iti.jet.ewd.ecom.exception.UserNotFoundException;
import gov.iti.jet.ewd.ecom.mapper.UserMapper;
import gov.iti.jet.ewd.ecom.repository.UserRepository;
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

    /************* Reference to UserMapper************/
    @Autowired
    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository , EmailServiceImpl emailServiceImpl) {
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
        hashedPassword = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(12));
        user.setPassword(hashedPassword);

        // Initialize cart with bidirectional relationship
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        //set token to this user
       // String token = jwtService.createToken(email, Duration.ofHours(24));

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
        return userRepository.updateCreditBalance(userId , amount);
    }

    @Override
    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException("email: " + email + " does not exist");
        }
    }
    @Override
        public void resetPassword(String resetToken, String newPassword) {
    }


    @Override
    public User authenticateUser(String email, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authenticateUser'");
    }


    @Override
    public void verifyEmail(String verificationToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyEmail'");
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
        session.setAttribute("user", userDto);
        return userDto;
    }

    @Override
    public void logout(HttpSession session)
    {
        //save cart by id in DB

        // then terminate session
        session.invalidate();
    }


}
