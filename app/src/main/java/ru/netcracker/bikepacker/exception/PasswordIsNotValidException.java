package ru.netcracker.bikepacker.exception;

public class PasswordIsNotValidException extends BaseException {
    public PasswordIsNotValidException() {
        description = "This password is unvalid or less then 8 characters";
    }
}
