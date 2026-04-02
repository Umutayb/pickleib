package pickleib.runner;

import context.ContextStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pickleib.annotations.ContextValue;

import java.util.List;

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

    // --- Edge case: unsupported/complex type logs warning, does not crash ---

    static class UnsupportedTypeTarget {
        @ContextValue("test-key")
        List<String> complexField;

        @ContextValue("test-key")
        String normalField;
    }

    @Test
    void injectFields_handles_unsupported_type_without_crashing() {
        UnsupportedTypeTarget target = new UnsupportedTypeTarget();
        assertDoesNotThrow(() -> ContextValueInjector.injectFields(target));
        // The normal String field should still be injected despite the complex field failing
        assertEquals("hello", target.normalField);
    }

    // --- Edge case: private fields in superclass hierarchy ---

    static class SuperClass {
        @ContextValue("test-key")
        private String superField;

        String getSuperField() {
            return superField;
        }
    }

    static class ChildClass extends SuperClass {
        @ContextValue(value = "child-key", defaultValue = "child-default")
        private String childField;

        String getChildField() {
            return childField;
        }
    }

    @Test
    void injectFields_injects_private_fields_in_superclass() {
        ChildClass target = new ChildClass();
        ContextValueInjector.injectFields(target);
        assertEquals("hello", target.getSuperField());
        assertEquals("child-default", target.getChildField());
    }

    // --- Edge case: fields without @ContextValue are left untouched ---

    static class MixedFieldsTarget {
        @ContextValue("test-key")
        String annotated;

        String plainString = "untouched";
        int plainInt = 42;
        Object plainObject = new Object();
    }

    @Test
    void injectFields_leaves_non_annotated_fields_untouched() {
        MixedFieldsTarget target = new MixedFieldsTarget();
        Object originalObject = target.plainObject;
        ContextValueInjector.injectFields(target);

        assertEquals("hello", target.annotated);
        assertEquals("untouched", target.plainString);
        assertEquals(42, target.plainInt);
        assertSame(originalObject, target.plainObject);
    }

    // --- Edge case: null value from ContextStore (key absent, no default) ---

    static class NullValueTarget {
        @ContextValue(value = "nonexistent-key", defaultValue = "")
        String fieldWithEmptyDefault;

        @ContextValue("another-nonexistent-key")
        String fieldWithNoDefault;
    }

    @Test
    void injectFields_handles_null_or_empty_context_values_gracefully() {
        NullValueTarget target = new NullValueTarget();
        assertDoesNotThrow(() -> ContextValueInjector.injectFields(target));
        // With empty default, field gets empty string
        assertEquals("", target.fieldWithEmptyDefault);
    }

    // --- Edge case: contextCheck resolves CONTEXT- prefix patterns ---

    @Test
    void injectFields_resolves_context_prefix_in_value() {
        // Store a value that will be looked up via CONTEXT- prefix
        ContextStore.put("resolved-key", "resolved-value");
        // Store a value whose content is a CONTEXT- reference
        ContextStore.put("indirect-key", "CONTEXT-resolved-key");

        IndirectContextTarget target = new IndirectContextTarget();
        ContextValueInjector.injectFields(target);
        // contextCheck should resolve "CONTEXT-resolved-key" -> "resolved-value"
        assertEquals("resolved-value", target.indirectField);
    }

    static class IndirectContextTarget {
        @ContextValue("indirect-key")
        String indirectField;
    }

    @Test
    void injectFields_resolves_context_prefix_in_default_value() {
        ContextStore.put("default-resolved", "from-store");

        ContextDefaultTarget target = new ContextDefaultTarget();
        ContextValueInjector.injectFields(target);
        // defaultValue "CONTEXT-default-resolved" goes through contextCheck -> "from-store"
        assertEquals("from-store", target.field);
    }

    static class ContextDefaultTarget {
        @ContextValue(value = "key-that-does-not-exist", defaultValue = "CONTEXT-default-resolved")
        String field;
    }

    // --- Edge case: deep superclass hierarchy (grandparent) ---

    static class GrandparentClass {
        @ContextValue("test-key")
        private String grandparentField;

        String getGrandparentField() {
            return grandparentField;
        }
    }

    static class ParentClass extends GrandparentClass {
        @ContextValue(value = "parent-key", defaultValue = "parent-val")
        private String parentField;

        String getParentField() {
            return parentField;
        }
    }

    static class GrandchildClass extends ParentClass {
        @ContextValue(value = "grandchild-key", defaultValue = "gc-val")
        String grandchildField;
    }

    @Test
    void injectFields_scans_entire_class_hierarchy() {
        GrandchildClass target = new GrandchildClass();
        ContextValueInjector.injectFields(target);
        assertEquals("hello", target.getGrandparentField());
        assertEquals("parent-val", target.getParentField());
        assertEquals("gc-val", target.grandchildField);
    }
}
