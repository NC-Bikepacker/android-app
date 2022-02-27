package ru.netcracker.bikepacker.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmailValidationService {

    private EmailValidationService() {}

    private static String regex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
            + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    public static boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}