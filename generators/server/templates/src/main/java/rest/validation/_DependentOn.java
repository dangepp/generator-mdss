package <%= config.packages.restValidation %>;

import java.lang.annotation.*;

/**
 * Describes a dependency of a category to a feature of a particular category.
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DependentOn {

    /**
     * Describes the dependency, has to be in the form of 
     * 'categoryField.featureValue'
     * @return - the value must only be given if the dependency is fulfilled.
     */
    String value();

    /**
     * Describes an optional atLeast constraint.
     * @return - size the collection has to have at least if the dependency is fulfilled.
     */
    int atLeast() default 0;

}
