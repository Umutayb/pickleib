package pickleib.annotations;

import pickleib.enums.Platform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a class as a mobile screen object for automatic registration. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScreenObject {
    /** @return the target platform for this screen object */
    Platform platform() default Platform.android;
    /** @return custom registry name (defaults to class name) */
    String name() default "";
}
