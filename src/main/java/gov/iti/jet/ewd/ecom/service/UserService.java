package gov.iti.jet.ewd.ecom.service;

import gov.iti.jet.ewd.ecom.dto.CreditBalanceDto;
import gov.iti.jet.ewd.ecom.dto.ChangePasswordDto;
import gov.iti.jet.ewd.ecom.dto.LoginRequestDto;
import gov.iti.jet.ewd.ecom.dto.UserDto;
import gov.iti.jet.ewd.ecom.entity.User;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface UserService {

    public User getUserByEmail(String email);

    public User getUserById(int userId) ;

    public List<User> getAllUsers();

    User createUser(User user);

    public void forgotPassword(String email);
    boolean verifyOtp(String email, String otpCode);
    void resetPassword(String email, String newPassword);


    public double changeBalance(CreditBalanceDto creditBalanceDto, HttpSession session) ;

   public boolean emailExists(String email);

   public boolean updateProfile(UserDto user);

    // public User updateUserProfile(int userId, UserProfileDto profileDto) ;
    //public void changePassword(int userId, ChangePasswordDto changePasswordDto);


    /************************ AuthService (login , logout) ***************/
    UserDto login(LoginRequestDto loginRequestDto, HttpSession session);
    void logout(HttpSession session);


    void changePassword(int userId, ChangePasswordDto changePasswordDto);


}
