package ru.netcracker.bikepacker.exception;

public class BaseException extends Exception {
    protected String description;

    public String getDescription() {
        return description;
    }
}
