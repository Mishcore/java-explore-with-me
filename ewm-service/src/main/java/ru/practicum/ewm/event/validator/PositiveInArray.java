package ru.practicum.ewm.event.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = IdsArrayValidator.class)
@Documented
public @interface PositiveInArray {

    String message() default "Массив идентификаторов не может содержать число равное 0 или отрицательное";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
