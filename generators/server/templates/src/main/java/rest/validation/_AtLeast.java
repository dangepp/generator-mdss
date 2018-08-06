package <%= config.packages.restValidation %>;

import java.lang.annotation.*;

/**
 * The collection has to hold at least x elements.
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeast {

    int value();

}
