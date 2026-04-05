package pickleib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a test runner class for automatic Pickleib element repository setup. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Pickleib {
    /** @return packages to scan for {@code @PageObject} / {@code @ScreenObject} classes */
    String[] scan() default {};
    /** @return whether to enable built-in Cucumber step definitions */
    boolean builtInSteps() default true;
    /** @return path to the {@code page-repository.json} file */
    String pageRepository() default "";
}
