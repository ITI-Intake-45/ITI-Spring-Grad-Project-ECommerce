package gov.iti.jet.ewd.ecom.exception;

public class UserAlreadyExistException  extends RuntimeException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
