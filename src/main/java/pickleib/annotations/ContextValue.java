package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects a value from the {@link context.ContextStore} into the annotated field.
 * Supports {@code {{key}}} replacement patterns.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ContextValue {
    /** @return the context key to look up */
    String value();
    /** @return the fallback value if the key is not found */
    String defaultValue() default "";
}
