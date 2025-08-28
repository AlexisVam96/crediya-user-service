package co.com.crediya.model.exception;

public class UserCustomException extends Exception{

    private String code;

    public UserCustomException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
