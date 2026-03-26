package pickleib.runner;

import context.ContextStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pickleib.annotations.ContextValue;
import static org.junit.jupiter.api.Assertions.*;

class ContextValueInjectorTest {

    static class TestTarget {
        @ContextValue("test-key")
        String injectedValue;

        @ContextValue(value = "missing-key", defaultValue = "fallback")
        String withDefault;

        String notAnnotated = "original";
    }

    @BeforeEach
    void setUp() {
        ContextStore.put("test-key", "hello");
    }

    @Test
    void injectFields_sets_annotated_field() {
        TestTarget target = new TestTarget();
        ContextValueInjector.injectFields(target);
        assertEquals("hello", target.injectedValue);
    }

    @Test
    void injectFields_uses_default_when_key_missing() {
        TestTarget target = new TestTarget();
        ContextValueInjector.injectFields(target);
        assertEquals("fallback", target.withDefault);
    }

    @Test
    void injectFields_ignores_non_annotated_fields() {
        TestTarget target = new TestTarget();
        ContextValueInjector.injectFields(target);
        assertEquals("original", target.notAnnotated);
    }
}
