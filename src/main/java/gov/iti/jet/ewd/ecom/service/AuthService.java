package gov.iti.jet.ewd.ecom.service;

import jakarta.servlet.http.HttpSession;

public interface AuthService {
    boolean authenticateAdmin(String username, String password, HttpSession session);
}