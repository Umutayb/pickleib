package pickleib.annotations;

import pickleib.enums.Platform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a class as a page object for automatic registration by {@link pickleib.runner.PickleibRunner}. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageObject {
    /** @return the target platform for this page object */
    Platform platform() default Platform.web;
    /** @return custom registry name (defaults to class name) */
    String name() default "";
}
