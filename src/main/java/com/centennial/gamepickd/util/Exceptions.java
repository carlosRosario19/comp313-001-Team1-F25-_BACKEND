package com.centennial.gamepickd.util;

import java.io.IOException;

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

    public static class GameAlreadyExistsException extends Exception {
        public GameAlreadyExistsException(String message) { super(message);}
    }

    public static class ContributorNotFoundException extends Exception {
        public ContributorNotFoundException(String message) { super(message);}
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class StorageFileNotFoundException extends Exception {
        public StorageFileNotFoundException(String message) {
            super(message);
        }

        public StorageFileNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
