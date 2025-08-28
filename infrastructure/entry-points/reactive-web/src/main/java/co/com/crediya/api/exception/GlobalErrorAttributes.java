package co.com.crediya.api.exception;

import co.com.crediya.model.exception.UserCustomException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options){
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        HttpStatus status = mapToHttpStatus(error);

        errorAttributes.put("timestamp", Instant.now().toString());
        errorAttributes.put("path", request.path());
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
        errorAttributes.put("message", error.getMessage());

        if (error instanceof UserCustomException ex) {
            errorAttributes.put("type", ex.getType());
        }

        return errorAttributes;
    }

    private HttpStatus mapToHttpStatus(Throwable error) {
        if (error instanceof UserCustomException ex) {
            return switch (ex.getType().name()) {
                case "VALIDATION" -> HttpStatus.BAD_REQUEST;
                case "NOT_FOUND"  -> HttpStatus.NOT_FOUND;
                case "AUTH"       -> HttpStatus.UNAUTHORIZED;
                default           -> HttpStatus.INTERNAL_SERVER_ERROR;
            };
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
