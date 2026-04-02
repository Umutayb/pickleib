package pickleib.runner;

import context.ContextStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pickleib.annotations.ContextValue;
import pickleib.annotations.PageObject;
import pickleib.annotations.Pickleib;

import static org.junit.jupiter.api.Assertions.*;

@Pickleib(scan = "pickleib.runner")
@ExtendWith(PickleibRunner.class)
class PickleibRunnerIntegrationTest {

    @PageObject
    static class IntegrationTestPage {
        // No @FindBy — just testing registration
    }

    @ContextValue(value = "integration-test-key", defaultValue = "test-value")
    String injectedValue;

    @Test
    void runner_discovers_page_objects() {
        assertTrue(PickleibRunner.getRegistry().isRegistered("IntegrationTestPage"));
    }

    @Test
    void runner_injects_context_values() {
        assertEquals("test-value", injectedValue);
    }

    @Test
    void runner_injects_context_values_from_store() {
        ContextStore.put("integration-test-key", "from-store");
        ContextValueInjector.injectFields(this);
        assertEquals("from-store", injectedValue);
    }

    @Test
    void registry_provides_element_repository() {
        assertNotNull(PickleibRunner.getRegistry());
    }
}
