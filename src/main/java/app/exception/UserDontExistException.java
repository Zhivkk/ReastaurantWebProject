package app.exception;

public class UserDontExistException extends RuntimeException{

    public UserDontExistException(String message) {
        super(message);
    }

    public UserDontExistException() {
    }
}
