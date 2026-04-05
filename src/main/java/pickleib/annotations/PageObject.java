package pickleib.annotations;

import pickleib.enums.Platform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageObject {
    Platform platform() default Platform.web;
    String name() default "";
}
