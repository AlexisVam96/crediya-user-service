package co.com.crediya.model.exception;

public class UserCustomException extends Exception{

    private ErrorType type;

    public UserCustomException(String message, ErrorType type) {
        super(message);
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }
}
