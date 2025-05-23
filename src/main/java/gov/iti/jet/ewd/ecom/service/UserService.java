package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(int id);
}
