package gov.iti.jet.ewd.ecom.service;

import jakarta.servlet.http.HttpSession;

public interface OrderService {

    String checkout(HttpSession session, int userId);
}
