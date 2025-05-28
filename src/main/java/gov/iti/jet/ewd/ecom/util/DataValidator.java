package gov.iti.jet.ewd.ecom.util;

public class DataValidator {

    public static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must start from 1");
        }
    }
}
