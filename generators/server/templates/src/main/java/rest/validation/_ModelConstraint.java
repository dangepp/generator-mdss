package <%= config.packages.restValidation %>;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Constraint for the validation fo Model classes
 */
@Documented
@Constraint(validatedBy = {DiseaseModelValidator.class, PatientModelValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelConstraint {
    String message() default "Invalid model";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

