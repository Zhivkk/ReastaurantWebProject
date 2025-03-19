package app.exception;

public class DomainException extends RuntimeException{

    //Собствено изключение
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
