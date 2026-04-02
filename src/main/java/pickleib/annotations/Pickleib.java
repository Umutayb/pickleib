package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Pickleib {
    String[] scan() default {};
    boolean builtInSteps() default true;
    String pageRepository() default "";
}
