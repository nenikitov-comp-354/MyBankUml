package bank.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class TypeValidator {
    private TypeValidator() {
    }

    public static void validateId(String description, int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(description + " `" + id + "` is not a valid SQL id (must be > 0)");
        }
    }

    public static void validateNotNull(String description, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(description + " is null");
        }
    }

    private static final Pattern NON_EMPTY_STRING = Pattern.compile("^\\S(?:.*\\S)?$");

    public static void validateNonEmptyText(String description, String text) {
        TypeValidator.validateNotNull(description, text);

        if (!NON_EMPTY_STRING.matcher(text).matches()) {
            throw new IllegalArgumentException(
                    description + " `" + text + "` is blank or starts and ends with trailing spaces");
        }
    }

    private static final Pattern SOCIAL_INSURANCE_NUMBER = Pattern.compile("^\\d{3}-\\d{3}-\\d{3}$");

    public static void validateSocialInsuranceNumber(String description, String socialInsuranceNumber) {
        TypeValidator.validateNotNull(description, socialInsuranceNumber);

        if (!SOCIAL_INSURANCE_NUMBER.matcher(socialInsuranceNumber).matches()) {
            throw new IllegalArgumentException(description + " `" + socialInsuranceNumber
                    + "` does not match social insurance format of 3 groups of 3 digits separated by `-`");
        }
    }

    private static final Pattern PHONE = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    public static void validatePhone(String description, String phone) {
        TypeValidator.validateNotNull(description, phone);

        if (!PHONE.matcher(phone).matches()) {
            throw new IllegalArgumentException(
                    description + " `" + phone + "` does not match phone number format of `+` followed by 2-15 digits");
        }
    }

    private static final Pattern EMAIL = Pattern.compile("^.+@.+$");

    public static void validateEmail(String description, String email) {
        TypeValidator.validateNotNull(description, email);

        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException(description + " `" + email
                    + "` does not match email format of some text followed by `@` followed by some text");
        }
    }

    public static void validatePositiveMoney(String description, BigDecimal money) {
        TypeValidator.validateNotNull(description, money);

        if (money.signum() < 0) {
            throw new IllegalArgumentException(description + " `" + money + "` is not positive or zero");
        }
    }

    public static void validatePositiveInteger(String description, int integer) {
        if (integer < 0) {
            throw new IllegalArgumentException(description + " `" + integer + "` is not positive");
        }
    }
}
