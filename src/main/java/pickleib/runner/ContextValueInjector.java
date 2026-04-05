package pickleib.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import context.ContextStore;
import pickleib.annotations.ContextValue;
import utils.Printer;
import java.lang.reflect.Field;
import static utils.StringUtilities.contextCheck;

/**
 * Processes {@link ContextValue} annotations by injecting values from the {@link ContextStore}.
 */
public class ContextValueInjector {

    private static final Printer log = new Printer(ContextValueInjector.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Injects {@link ContextValue}-annotated fields on the given instance.
     * Scans the instance's class hierarchy (including superclasses) for annotated fields.
     *
     * @param instance the object whose annotated fields will be injected
     */
    public static void injectFields(Object instance) {
        Class<?> clazz = instance.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                ContextValue annotation = field.getAnnotation(ContextValue.class);
                if (annotation == null) continue;

                String value = contextCheck(ContextStore.get(annotation.value(), annotation.defaultValue()));
                try {
                    field.setAccessible(true);
                    field.set(instance, convertValue(value, field.getType()));
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log.warning("Failed to inject @ContextValue for field '" + field.getName() + "': " + e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static <T> T convertValue(String value, Class<T> targetType) {
        return objectMapper.convertValue(value, targetType);
    }
}
