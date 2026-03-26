package pickleib.runner;

import context.ContextStore;
import pickleib.annotations.ContextValue;
import utils.Printer;
import java.lang.reflect.Field;

public class ContextValueInjector {

    private static final Printer log = new Printer(ContextValueInjector.class);

    public static void injectFields(Object instance) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            ContextValue annotation = field.getAnnotation(ContextValue.class);
            if (annotation == null) continue;

            String value = ContextStore.get(annotation.value(), annotation.defaultValue());
            try {
                field.setAccessible(true);
                field.set(instance, convertValue(value, field.getType()));
            } catch (IllegalAccessException e) {
                log.warning("Failed to inject @ContextValue for field: " + field.getName());
            }
        }
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        return value;
    }
}
