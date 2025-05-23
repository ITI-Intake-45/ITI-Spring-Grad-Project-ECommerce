package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.entity.User;
import gov.iti.jet.ewd.ecom.exception.UserAlreadyExistException;
import gov.iti.jet.ewd.ecom.exception.UserNotFoundException;
import gov.iti.jet.ewd.ecom.repository.UserRepository;
import gov.iti.jet.ewd.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistException("User with email already exists");
        }
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
}
