# Pickleib 🥒

[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/pickleib?color=brightgreen&label=pickleib)](https://mvnrepository.com/artifact/io.github.umutayb/pickleib/latest)

**Pickleib** is a comprehensive, polymorphic test automation utility library designed to streamline **Web**, **Mobile**, **Desktop**, **API** and **Database** testing.

It acts as a robust wrapper around Selenium and Appium, allowing you to write interaction-agnostic code that works across platforms. It offers a unique **"Hybrid" Page Object Model** approach, letting you choose between a classic Java implementation or a "Low-Code" JSON-based element definition.


### ⚡Template Project
To see **Pickleib** in action, and to use it as a no code solution, check out the 👉 **[test-automation-template](https://github.com/Umutayb/test-automation-template)**

---

## 🚀 Key Features

* **🌐 Polymorphic Interactions:** Write tests that run on web, mobile & desktop platforms using a unified interface.
* **🏗️ Hybrid Page Object Model:** Classic Java POM with `@FindBy`, or Low-Code JSON-based selectors — your choice.
* **📋 Built-in Step Definitions:** 68 pre-built Cucumber steps covering click, fill, verify, scroll, wait, select, context, and more.
* **🤖 Annotation-Driven Runner:** `@Pickleib`, `@PageObject`, `@ContextValue` annotations eliminate boilerplate.
* **🚗 Smart Driver Management:** Automated `WebDriver` and `AppiumDriver` lifecycle handling.
* **❤️‍🩹 Self-Healing Utilities:** Retry mechanisms for `StaleElementReferenceException` and `FluentWait` synchronization.
* **🧳 Context Management:** Global `ContextStore` for sharing data between steps and configuring runtime variables.
* **🧵 Thread-Safe Parallel Execution:** `ThreadLocal` driver singletons — run tests in parallel without interference.
* **🛠️ Cross-Functional Testing:** API testing (Wasapi), Database (JDBC), Email client, Web Data Layer validation.

---

## 📦 Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.umutayb</groupId>
    <artifactId>pickleib</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Claude Code Skill (Auto-Install)

Pickleib ships with a Claude Code agent skill for AI-assisted test generation. To auto-extract it during build, add to your `pom.xml` plugins:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-antrun-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>install-claude-skills</id>
            <phase>initialize</phase>
            <goals><goal>run</goal></goals>
            <configuration>
                <target>
                    <mkdir dir="${project.basedir}/skills"/>
                    <unjar src="${local.repo}/io/github/umutayb/pickleib/${pickleib.version}/pickleib-${pickleib.version}.jar"
                           dest="${project.basedir}/.claude-extract">
                        <patternset>
                            <include name="META-INF/claude/skills/**"/>
                        </patternset>
                    </unjar>
                    <copy todir="${project.basedir}/skills" overwrite="true" failonerror="false">
                        <fileset dir="${project.basedir}/.claude-extract/META-INF/claude/skills"/>
                    </copy>
                    <delete dir="${project.basedir}/.claude-extract" quiet="true"/>
                </target>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Add `local.repo` to your properties: `<local.repo>${settings.localRepository}</local.repo>`

After `mvn initialize`, the skill will be available at `skills/pickleib/SKILL.md`. Add `skills/` to `.gitignore`.

---

## 🏁 Getting Started

There are two main approaches to defining your elements. Both give you access to the same built-in step library.

| Approach | Best For | Element Definition | Java Code Needed |
| :--- | :--- | :--- | :--- |
| **JSON Repository** (Low-Code) | Quick start, small teams, simple pages | `page-repository.json` | Minimal (just `CommonSteps` + `Hooks`) |
| **Page Objects** (Classic POM) | Large projects, complex interactions, IDE support | Java classes with `@FindBy` | Page classes + `ObjectRepository` |

Pick the one that fits your workflow — or mix both in the same project.

---

### Quick Start: JSON Repository (Low-Code)

Get tests running with **zero page classes**. Define elements in JSON, add a Hooks file, and write Gherkin.

#### Step 1: Create `page-repository.json`

```json
{
  "pages": [
    {
      "name": "LoginPage",
      "platform": "web",
      "elements": [
        {
          "elementName": "usernameInput",
          "selectors": { "web": [{ "css": "#user-name" }] }
        },
        {
          "elementName": "passwordInput",
          "selectors": { "web": [{ "css": "#password" }] }
        },
        {
          "elementName": "loginButton",
          "selectors": { "web": [{ "id": "login-button" }] }
        },
        {
          "elementName": "welcomeMessage",
          "selectors": { "web": [{ "css": ".welcome" }] }
        }
      ]
    }
  ]
}
```

Each page has a `name`, `platform`, and a list of `elements`. Each element supports multiple selector types (`css`, `id`, `xpath`, `accessibilityId`) and platform-specific selectors (`web`, `android`, `ios`).

#### Step 2: Create `Hooks.java`

```java
import io.cucumber.java.*;
import pickleib.web.driver.PickleibWebDriver;

public class Hooks {
    @Before
    public void setup() {
        PickleibWebDriver.initialize();
    }

    @After
    public void teardown() {
        PickleibWebDriver.terminate();
    }
}
```

#### Step 3: Write your feature file

```gherkin
@Web-UI
Scenario: Login flow
  * Navigate to url: https://example.com
  * Fill input usernameInput on the LoginPage with text: admin
  * Fill input passwordInput on the LoginPage with text: secret
  * Click the loginButton on the LoginPage
  * Verify the text of welcomeMessage on the LoginPage contains: Welcome
```

#### Step 4: Configure the Test Runner

Use `@Pickleib(pageRepository = ...)` to wire the JSON file automatically — no `CommonSteps` class needed:

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.extension.ExtendWith;
import pickleib.annotations.Pickleib;
import pickleib.runner.PickleibRunner;

@RunWith(Cucumber.class)
@Pickleib(pageRepository = "src/test/resources/page-repository.json")
@ExtendWith(PickleibRunner.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"steps", "pickleib.steps"},
    plugin = {"json:target/reports/Cucumber.json"}
)
public class TestRunner {}
```

**Important:** Add `"pickleib.steps"` to the glue path. This activates the built-in step definitions.

#### Step 5: Run

```shell
mvn test -Dcucumber.filter.tags="@Web-UI" -Dbrowser=chrome
```

---

### Quick Start: Page Objects (Classic POM)

Use standard Java classes with `@FindBy` annotations for IDE autocomplete, compile-time safety, and custom page methods.

#### Step 1: Create Page Classes

```java
package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

public class LoginPage extends PickleibPageObject {
    @FindBy(id = "user-name")
    public WebElement usernameInput;

    @FindBy(css = "#password")
    public WebElement passwordInput;

    @FindBy(id = "login-button")
    public WebElement loginButton;

    @FindBy(css = ".welcome")
    public WebElement welcomeMessage;
}
```

#### Step 2: Create an Object Repository

Register your page classes in a single class. Pickleib uses reflection to scan this.

```java
package common;

import pages.*;
import pickleib.utilities.interfaces.repository.PageObjectRepository;

public class ObjectRepository implements PageObjectRepository {
    public LoginPage loginPage;
    public DashboardPage dashboardPage;
    // Add all page objects as fields
}
```

#### Step 3: Create `CommonSteps.java`

```java
import common.ObjectRepository;
import pickleib.utilities.steps.PickleibSteps;

public class CommonSteps extends PickleibSteps {
    public CommonSteps() {
        super(ObjectRepository.class);
    }
}
```

Same as the JSON approach — one class gives you all 68 built-in steps.

#### Step 4: Create `Hooks.java`

```java
import io.cucumber.java.*;
import pickleib.web.driver.PickleibWebDriver;

public class Hooks {
    @Before
    public void setup() {
        PickleibWebDriver.initialize();
    }

    @After
    public void teardown() {
        PickleibWebDriver.terminate();
    }
}
```

#### Step 5: Write feature files and run

The Gherkin syntax is identical regardless of which approach you use:

```gherkin
@Web-UI
Scenario: Login flow
  * Navigate to url: https://example.com
  * Fill input usernameInput on the LoginPage with text: admin
  * Fill input passwordInput on the LoginPage with text: secret
  * Click the loginButton on the LoginPage
  * Verify the text of welcomeMessage on the LoginPage contains: Welcome
```

---

## 📋 Built-in Step Library

All steps work with both JSON and Page Object approaches. Add `pickleib.steps` to your Cucumber glue path to activate them.

### Navigation

| Step | Description |
| :--- | :--- |
| `Navigate to url: {url}` | Open a URL |
| `Navigate to test url` | Open the URL from `test-url` context key |
| `Go to the {pagePath} page` | Navigate to a relative path |
| `Refresh the page` | Reload the current page |
| `Navigate browser backwards/forwards` | Browser back/forward |
| `Switch to the next tab` | Switch to the next browser tab |
| `Switch back to the parent tab` | Return to the original tab |
| `Save current url to context` | Store current URL in ContextStore |

### Click / Tap

| Step | Description |
| :--- | :--- |
| `Click the {element} on the {Page}` | Click an element on a page |
| `Click button with {text} text` | Click by visible text |
| `Click listed element {name} from {list} list on the {Page}` | Click element from a list |
| `If present, click the {element} on the {Page}` | Click only if element exists |
| `If enabled, click the {element} on the {Page}` | Click only if element is enabled |
| `Click towards the {element} on the {Page}` | Scroll-to-click |

### Input / Form

| Step | Description |
| :--- | :--- |
| `Fill input {element} on the {Page} with text: {value}` | Fill a text input |
| `Fill input {element} on the {Page} with verified text: {value}` | Fill and verify the value was set |
| `Fill form input on the {Page}` | Fill multiple inputs from a table (see below) |
| `Select option {text} from {element} on the {Page}` | Select from a dropdown |

**Form fill table format:**

```gherkin
* Fill form input on the LoginPage
  | element       | input    |
  | usernameInput | admin    |
  | passwordInput | secret   |
```

### Verify

| Step | Description |
| :--- | :--- |
| `Verify the text of {element} on the {Page} to be: {text}` | Exact text match |
| `Verify the text of {element} on the {Page} contains: {text}` | Partial text match |
| `Verify presence of element {element} on the {Page}` | Element exists in DOM |
| `Verify absence of element {element} on the {Page}` | Element not in DOM |
| `Verify that element {element} on the {Page} is in {state} state` | Check enabled/displayed/selected/disabled/absent |
| `Verify that element {element} on the {Page} has {value} value for its {attribute} attribute` | Check attribute value |
| `Verify the url contains with the text {text}` | URL assertion |

### Wait

| Step | Description |
| :--- | :--- |
| `Wait {n} seconds` | Hard wait |
| `Wait for element {element} on the {Page} to be visible` | Wait until visible |
| `Wait for absence of element {element} on the {Page}` | Wait until gone |
| `Wait until element {element} on the {Page} has {value} value for its {attribute} attribute` | Wait for attribute |

### Scroll / Swipe

| Step | Description |
| :--- | :--- |
| `Scroll up/down/left/right using web/mobile driver` | Directional scroll |
| `Scroll until listed {element} from {list} is found on the {Page}` | Scroll to find element |
| `Center the {element} on the {Page}` | Scroll element into center view |

### Context Store

| Step | Description |
| :--- | :--- |
| `Update context {key} -> {value}` | Store a key-value pair |
| `Save context value from {key} context key to {newKey}` | Copy a context value |
| `Acquire the {attribute} attribute of {element} on the {Page}` | Save attribute to context |

Use `CONTEXT-{key}` in any step value to reference stored context values:
```gherkin
* Update context testUser -> admin
* Fill input usernameInput on the LoginPage with text: CONTEXT-testUser
```

### Other

| Step | Description |
| :--- | :--- |
| `Upload file on input {element} on the {Page} with file: {path}` | File upload |
| `Execute JS command: {script}` | Run JavaScript |
| `Set window width & height as {w} & {h}` | Resize browser |
| `Add the following cookies:` | Add cookies from table |
| `Delete cookies` | Clear all cookies |

---

## 🏷️ Annotations

### `@Pickleib`
Marks a test class for automatic page object scanning and registration. Used with `PickleibRunner`:
```java
// Page Object approach
@Pickleib(scanPackages = {"pages", "screens"})
@ExtendWith(PickleibRunner.class)
public class MyTest { ... }

// JSON Repository approach
@Pickleib(pageRepository = "src/test/resources/page-repository.json")
@ExtendWith(PickleibRunner.class)
public class MyTest { ... }
```

| Attribute | Description | Default |
| :--- | :--- | :--- |
| `scanPackages` | Packages to scan for `@PageObject` / `@ScreenObject` classes | `{}` (infers from test class package) |
| `pageRepository` | Path to `page-repository.json` (JSON approach) | `""` (disabled) |
| `builtInSteps` | Enable built-in Cucumber step definitions | `true` |

### `@PageObject`
Mark any class as a page object — no inheritance required:
```java
@PageObject
public class LoginPage { ... }

@PageObject(platform = Platform.ios, name = "Login")
public class LoginPageIOS { ... }
```

| Attribute | Description | Default |
| :--- | :--- | :--- |
| `platform` | Target platform (`Platform` enum) | `Platform.web` |
| `name` | Custom registry name (defaults to class name) | `""` |

### `@ScreenObject`
Mark a class as a mobile screen object:
```java
@ScreenObject
public class HomeScreen { ... }
```

| Attribute | Description | Default |
| :--- | :--- | :--- |
| `platform` | Target platform (`Platform` enum) | `Platform.android` |
| `name` | Custom registry name (defaults to class name) | `""` |

### `@ContextValue`
Inject values from the `ContextStore` directly into fields. Supports `{{key}}` replacement patterns:
```java
@ContextValue("test-url")
private String testUrl;

@ContextValue(value = "timeout", defaultValue = "15000")
private long timeout;
```

### `Platform` Enum
Supported platform values: `web` | `android` | `ios` | `macos` | `windows`

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Your Test Project                   │
│  ┌──────────┐   ┌──────────┐  ┌───────────────────┐ │
│  │ Feature  │   │  Hooks   │  │  Page Objects     │ │
│  │  Files   │   │          │  │  (Java or JSON)   │ │
│  │(.feature)│   │(@Before/ │  │                   │ │
│  │          │   │ @After)  │  │                   │ │
│  └────┬─────┘   └────┬─────┘  └────────┬──────────┘ │
└───────┼──────────────┼─────────────────┼────────────┘
        │              │                 │
┌───────┼──────────────┼─────────────────┼────────────┐
│       ▼              ▼                 ▼   Pickleib │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────┐  │
│  │BuiltIn   │  │Pickleib  │  │ PageObjectRegistry│  │
│  │Steps     │  │WebDriver │  │ or PageObjectJson │  │
│  │(68 steps)│  │(ThreadL) │  │(ElementRepository)│  │
│  └────┬─────┘  └──────────┘  └────────┬──────────┘  │
│       │                               │             │
│       ▼                               ▼             │
│  ┌──────────────────────────────────────────────┐   │
│  │           InteractionBase                    │   │
│  │    ┌───────────────┐  ┌────────────────┐     │   │
│  │    │WebInteractions│  │PlatformInteract│     │   │
│  │    │  (Selenium)   │  │    (Appium)    │     │   │
│  │    └──────┬────────┘  └───────┬────────┘     │   │
│  └───────────┼──────────────────┼───────────────┘   │
│              │                  │                    │
│  ┌───────────▼──────────────────▼───────────────┐   │
│  │              Utility Helpers                  │   │
│  │  ┌─────────┐ ┌──────────┐ ┌───────────────┐  │   │
│  │  │ClickHlp │ │InputHelp │ │ElementStateHlp│  │   │
│  │  └────┬────┘ └────┬─────┘ └───────┬───────┘  │   │
│  └───────┼───────────┼───────────────┼──────────┘   │
│          └───────────┼───────────────┘              │
│                      ▼                              │
│              ┌──────────────┐                       │
│              │  RetryPolicy │                       │
│              └──────────────┘                       │
└─────────────────────────────────────────────────────┘
```

---

## ⚙️ Configuration

Pickleib is configured via `pickleib.properties` in your resources directory. These are loaded into the global `ContextStore`.

### Web Driver

```properties
browser=chrome
headless=true
driver-timeout=15000
element-timeout=15000
frame-width=1920
frame-height=1080
delete-cookies=true
```

| Property | Description | Default |
| :--- | :--- | :--- |
| `browser` | `chrome`, `firefox`, `safari`, `edge` | `chrome` |
| `headless` | Run without a UI | `false` |
| `driver-timeout` | Implicit wait in ms | `15000` |
| `element-timeout` | Element interaction timeout in ms | `15000` |
| `frame-width` / `frame-height` | Browser window size | `1920` / `1080` |
| `driver-maximize` | Maximize window on startup | `false` |
| `delete-cookies` | Clear cookies before each test | `false` |
| `selenium-grid` | Use a remote Grid | `false` |
| `hub-url` | Grid hub URL | `""` |
| `mobile-mode` | Chrome mobile emulation | `false` |
| `emulated-device` | Device profile for emulation | `iPhone12Pro` |

### Mobile / Desktop (Appium)

```properties
device=MyApp
config=src/test/resources/configurations
use-appium2=true
address=0.0.0.0
port=4723
```

Create a JSON capability file at `{config}/{device}.json`:

```json
{
  "platformName": "iOS",
  "automationName": "XCUITest",
  "deviceName": "iPhone 14",
  "bundleId": "com.company.app",
  "app": "src/test/resources/apps/MyApp.app"
}
```

---

## 🧵 Parallel Execution

Driver singletons use `ThreadLocal` — parallel execution is safe out of the box.

```shell
mvn test -Dcucumber.execution.parallel.enabled=true -Dcucumber.execution.parallel.config.fixed.parallelism=4
```

---

## 💻 Local Development

This repository includes a sample test website:

```shell
docker-compose up --build -d
```

Access at 👉 **[http://localhost:7457](http://localhost:7457)**
