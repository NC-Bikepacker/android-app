package ru.netcracker.bikepacker.service;

public abstract class EmailValidationService {

    private static String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isEmailValid(String email) {
        if (email.matches(regexPattern)) {
            return true;
        }

        return false;
    }
}
