package com.centennial.gamepickd.util;

public class Exceptions {
    public static class EmailAlreadyExistsException extends Exception {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UsernameAlreadyExistsException extends Exception {
        public UsernameAlreadyExistsException(String message) {
            super(message);
        }
    }
}
