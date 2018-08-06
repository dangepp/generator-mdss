package <%= config.packages.restException %>;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * Exception Wrapper for UnprocessableEntityException and ConstraintViolationException
 * resuting in HttpStatus.UNPROCESSABLE_ENTITY.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, UnprocessableEntityException.class})
    public ResponseEntity<ExceptionWrapper> handleConflict(Throwable t) {
        return new ResponseEntity<>(new ExceptionWrapper(t), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Getter
    public static class ExceptionWrapper {
        private String message;
        private ExceptionWrapper(Throwable b){
            message = b.getMessage();
        }
    }
}
