# Pickleib 🥒

[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/pickleib?color=brightgreen&label=pickleib)](https://mvnrepository.com/artifact/io.github.umutayb/pickleib/latest)

**Pickleib** is a comprehensive, polymorphic test automation utility library designed to streamline **Web**, **Mobile**, **Desktop**, **API** and **Database** testing.

It acts as a robust wrapper around Selenium and Appium, allowing you to write interaction-agnostic code that works across platforms. It offers a unique **"Hybrid" Page Object Model** approach, letting you choose between a classic Java implementation or a "Low-Code" JSON-based element definition.


### ⚡Template Project
To see **Pickleib** in action, and to use it as a no code solution, check out the 👉 **[test-automation-template](https://github.com/Umutayb/test-automation-template)**

---

## 🚀 Key Features

Pickleib simplifies test design by offering ready-to-use driver management, powerful utilities, and flexible design patterns.

* **🌐 Polymorphic Interactions:** Write tests that run on web, mobile & desktop platforms using a unified interface (`PolymorphicUtilities`).
* **🏗️ Hybrid Page Object Model:**
  * **Classic POM:** Use standard Java classes with `@FindBy` annotations.
  * **Low-Code POM:** Define your pages and selectors in a single `page-repository.json` file—no page classes required!
* **🚗 Smart Driver Management:** Automated handling of `WebDriver` and `AppiumDriver` lifecycles.
* **❤️‍🩹 Self-Healing Utilities:** Built-in retry mechanisms for `StaleElementReferenceException` and intelligent `FluentWait` synchronization.
* **🧳 Context Management:** A global `ContextStore` for sharing data between steps and configuring run-time environment variables.
* **📝 Verbose Logging:** Automatically logs interactions (e.g., "Clicking 'loginButton' on 'LoginPage'") for easier debugging.
* **🤖 Annotation-Driven Runner:** Use `@Pickleib`, `@PageObject`, `@ContextValue` annotations to eliminate boilerplate — no ObjectRepository class needed, no page object inheritance required.
* **📋 Built-in Step Definitions:** 67 pre-built Cucumber steps covering click, fill, verify, scroll, wait, context, and more. Add `pickleib.steps` to your glue path and skip writing a CommonSteps class entirely.
* **🔄 Centralized Retry Policy:** All element interactions use a unified `RetryPolicy` with consistent timeout handling, logging, and exception management.
* **🧵 Thread-Safe Parallel Execution:** Driver singletons use `ThreadLocal` — run tests in parallel without driver interference.
* **🛠️ Cross-Functional Testing:**
  * 🔌 API testing via **Wasapi** (Retrofit)
  * 🗄️ Database interactions using **JDBC**
  * 📧 **Built in email client:** sending, receiving emails & HTML verification
  * 📊 **Web Data Layer Validation:** Verify events, values, and structures directly.

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Your Test Project                  │
│  ┌──────────┐   ┌──────────┐  ┌───────────────────┐ │
│  │ Feature  │   │  Hooks   │  │  Page Objects     │ │
│  │  Files   │   │          │  │  (@PageObject or  │ │
│  │(.feature)│   │(@Before/ │  │  @FindBy classes) │ │
│  │          │   │ @After)  │  │                   │ │
│  └────┬─────┘   └────┬─────┘  └────────┬──────────┘ │
└───────┼──────────────┼─────────────────┼────────────┘
        │              │                 │
┌───────┼──────────────┼─────────────────┼────────────┐
│       ▼              ▼                 ▼   Pickleib │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────┐  │
│  │BuiltIn   │  │Pickleib  │  │ PageObjectRegistry│  │
│  │Steps     │  │WebDriver │  │ or PageObjectJson │  │
│  │(67 steps)│  │(ThreadL) │  │(ElementRepository)│  │
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
│              │                  │                   │
│  ┌───────────▼──────────────────▼───────────────┐   │
│  │              Utility Helpers                 │   │
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

## 📦 Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.umutayb</groupId>
    <artifactId>pickleib</artifactId>
    <version>2.1.0</version>
</dependency>
```

or if you are using gradle:
```groovy
implementation 'io.github.umutayb:pickleib:2.0.9'
```

---

## 🏗️ Driver Setup (Hooks)

Manage the driver lifecycle using hooks. **Pickleib** handles the singleton initialization for you.

```java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pickleib.web.driver.PickleibWebDriver;
// import pickleib.mobile.driver.PickleibAppiumDriver;

public class Hooks {

    @BeforeEach
    public void start() {
        // Initialize Web Driver
        PickleibWebDriver.initialize();

        // OR Initialize Mobile/Desktop Driver
        // PickleibAppiumDriver.initialize();
    }

    @AfterEach
    public void kill() {
        // Terminate Web Driver
        PickleibWebDriver.terminate();

        // OR Terminate Mobile/Desktop Driver
        // PickleibAppiumDriver.terminate();
    }
}
```

---

## 📖 Usage: Defining Elements & Steps

Pickleib allows you to structure your Object Repository in multiple ways. Choose the one that fits your team's workflow.

### Method 1: Annotation-Driven with `PickleibRunner` (Recommended)

Use **Pickleib** annotations and the JUnit 5 extension — zero boilerplate.

**1. Annotate your Page/Screen Objects**

```java
@PageObject
public class LoginPage {
    @FindBy(id = "user-name")
    public WebElement usernameInput;

    @FindBy(css = "#login-button")
    public WebElement loginButton;
}

@ScreenObject(platform = Platform.ios)
public class HomeScreen {
    @AndroidFindBy(accessibility = "home_title")
    @iOSXCUITFindBy(accessibility = "home_title")
    public MobileElement title;
}
```

No inheritance needed. No ObjectRepository. Just annotate and go.

**2. Wire up with `@Pickleib` and `PickleibRunner`**

```java
@Pickleib(scanPackages = {"pages", "screens"})
@ExtendWith(PickleibRunner.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"steps", "pickleib.steps"}
)
public class TestRunner {}
```

`PickleibRunner` automatically:
- Scans the specified packages for `@PageObject` and `@ScreenObject` classes
- Registers them in the `PageObjectRegistry`
- Injects `@ContextValue` fields on test instances

**3. Write feature files — steps just work**

```gherkin
@Web-UI
Scenario: Login flow
  * Navigate to url: https://example.com
  * Fill input usernameInput on the LoginPage with text: admin
  * Fill input passwordInput on the LoginPage with text: secret
  * Click the loginButton on the LoginPage
  * Verify the text of welcomeMessage on the DashboardPage contains: Welcome
```

67 built-in steps are available immediately — no CommonSteps class needed.

---

### Method 2: The "Low-Code" JSON Repository

Define your elements in a JSON file. Pickleib will parse this file at runtime to locate elements, reducing Java boilerplate.

**1. Create `src/test/resources/page-repository.json`**

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
          "elementName": "loginButton",
          "selectors": { "web": [{ "id": "login-button" }] }
        }
      ]
    }
  ]
}
```

**2. Initialize `PickleibSteps` with the JSON path**

```java
import pickleib.utilities.steps.PickleibSteps;
import org.openqa.selenium.WebElement;

public class CommonSteps extends PickleibSteps {

    public CommonSteps() {
        // Point to your JSON file
        super("src/test/resources/page-repository.json");
    }

    @When("I click the {string} on the {string} page")
    public void clickTheButton(String buttonName, String pageName) {
        log.info("Clicking the " + buttonName + " on the " + pageName);

        // Acquire element dynamically from the JSON definition
        WebElement button = getElementRepository().acquireElementFromPage(buttonName, pageName);

        // Perform interaction
        getInteractions(button).clickElement(button);
    }
}
```

---

### Method 3: Classic Page Object Model

Use standard Java classes extending `PickleibPageObject` and register them in a central repository class.

**1. Create a Page Class**

```java
package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

public class LoginPage extends PickleibPageObject {

    @FindBy(id = "user-name")
    public WebElement usernameInput;

    @FindBy(css = "#login-button")
    public WebElement loginButton;
}
```

**2. Register in an Object Repository**

Create a class that implements `PageObjectRepository`. Declare your page classes as fields here. Pickleib uses reflection to scan this class.

```java
package common;

import pages.LoginPage;
import pickleib.utilities.interfaces.repository.PageObjectRepository;

public class ObjectRepository implements PageObjectRepository {

    // The framework will detect these fields via reflection
    public LoginPage loginPage;
}
```

**3. Initialize `PickleibSteps` with the Class Repository**

```java
import common.ObjectRepository;
import pickleib.utilities.steps.PickleibSteps;
import org.openqa.selenium.WebElement;

public class CommonSteps extends PickleibSteps {

    public CommonSteps() {
        // Point to your ObjectRepository class
        super(ObjectRepository.class);
    }

    @When("I click the {string} on the {string} page")
    public void clickTheButton(String buttonName, String pageName) {
        log.info("Clicking the " + buttonName + " on the " + pageName);

        // Acquire element dynamically via reflection
        WebElement button = getElementRepository().acquireElementFromPage(buttonName, pageName);

        getInteractions(button).clickElement(button);
    }
}
```

**Alternatively, you can instantiate page objects and call their methods directly.**

```java
import pages.HomePage;

public class HomePageSteps {

    HomePage homePage = new HomePage();

    @Given("I select the {string} category")
    public void selectCategory(String categoryName) {
        homePage.selectCategory(categoryName);
    }
}
```

---

## 🏷️ Annotations

Pickleib provides annotations to reduce boilerplate and wire up your test infrastructure declaratively.

### `@Pickleib`
Marks a test class for automatic page object scanning and registration. Used with `PickleibRunner`:
```java
@Pickleib(scanPackages = {"pages", "screens"})
@ExtendWith(PickleibRunner.class)
public class MyTest { ... }
```

| Attribute | Description | Default |
| :--- | :--- | :--- |
| `scanPackages` | Packages to scan for `@PageObject` / `@ScreenObject` classes | `{}` (infers from test class package) |
| `builtInSteps` | Enable built-in Cucumber step definitions | `true` |

### `@PageObject`
Mark any class as a page object — no inheritance required:
```java
@PageObject
public class LoginPage {
    @FindBy(id = "user-name")
    public WebElement usernameInput;
}

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
public class HomeScreen {
    @AndroidFindBy(accessibility = "home_title")
    public MobileElement title;
}
```

| Attribute | Description | Default |
| :--- | :--- | :--- |
| `platform` | Target platform (`Platform` enum) | `Platform.android` |
| `name` | Custom registry name (defaults to class name) | `""` |

### `@ContextValue`
Inject values from the `ContextStore` directly into fields. Supports context-key-value replacement (e.g., `{{key}}` patterns, random values, localization):
```java
@ContextValue("test-url")
private String testUrl;

@ContextValue(value = "timeout", defaultValue = "15000")
private long timeout;
```

### `@StepDefinitions`
Marks a class as containing Cucumber step definitions for auto-discovery by `PickleibRunner`.

### `Platform` Enum
Supported platform values used by `@PageObject` and `@ScreenObject`:
```
web | android | ios | macos | windows
```

---

## 🏃 Execution

### Cucumber Feature File
Regardless of the method chosen above, your Gherkin remains the same:

```gherkin
Background: Context user
  * Navigate to the test page

@Web-UI @Scenario-1
Scenario: Click interactions
  * I click the "loginButton" on the "LoginPage" page
```

### Test Runner Configuration

```java
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"steps", "hooks"},
        plugin = {"json:target/reports/Cucumber.json"},
        publish = true
)
public class TestRunner {}
```

### CLI Execution

Run tests via Maven, filtering by tags and browser.

**Run Tests**
```shell
  mvn clean test -Dcucumber.filter.tags="@Web-UI" -Dbrowser=chrome
```

### 🧵 Parallel Execution

Pickleib's driver singletons use `ThreadLocal`, making parallel test execution safe out of the box.

**Maven Surefire (JUnit 5):**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

**Cucumber Parallel:**
```shell
mvn test -Dcucumber.execution.parallel.enabled=true -Dcucumber.execution.parallel.config.fixed.parallelism=4
```

Each thread gets its own driver instance — no shared state, no interference.

---

## ⚙️ Session Configuration

Pickleib allows extensive customization via a `pickleib.properties` file located in your resources directory (e.g., `src/test/resources/pickleib.properties`). These properties are loaded into the global `ContextStore` and determine how your drivers (Web, Mobile & Desktop) are initialized.

### 🌐 Web Driver Customization

You can control browser type, window size, timeouts, and execution modes (Headless/Grid) using the following properties:

**`pickleib.properties` Example:**

```properties
# --- General Driver Settings ---
browser=chrome
headless=true
driver-timeout=15000
driver-maximize=false
frame-width=1920
frame-height=1080
delete-cookies=true

# --- Advanced Options ---
load-strategy=normal
web-driver-manager=false
selenium-log-level=off
```

---

### 📱 Mobile & Desktop Driver Customization (Appium)

For Mobile (Android/iOS) or Desktop (Windows/MacOS) automation, Pickleib separates the **Server Configuration** from the **Device Capabilities**.

#### 1. Define Server Properties
Set these in your `pickleib.properties` file:

```properties
# --- Appium Server Config ---
use-appium2=true
start-service=true
address=0.0.0.0
port=4723

# --- Device Selection ---
# Directory containing your capability JSON files
config=src/test/resources/configurations
# The name of the JSON file to load (without .json extension)
device=InventoryApp
```

### 🌐 Web Driver Configuration

These properties control the `WebDriverFactory`. They cover everything from basic browser selection to advanced proxy and grid configurations.

| Property | Description | Default |
| :--- | :--- | :--- |
| **Basic Setup** | | |
| `browser` | Target browser (`chrome`, `firefox`, `safari`, `edge`, `opera`) | `chrome` |
| `headless` | Run without a UI | `false` |
| `driver-timeout` | Implicit wait time in **milliseconds** | `15000` |
| `delete-cookies` | Delete all cookies before starting the test | `false` |
| `frame-width` | Browser window width (if not maximized) | `1920` |
| `frame-height` | Browser window height (if not maximized) | `1080` |
| `driver-maximize` | Maximize window on startup | `false` |
| **Advanced** | | |
| `load-strategy` | Page load strategy (`normal`, `eager`, `none`) | `normal` |
| `web-driver-manager` | Use WDM to download driver binaries automatically | `false` |
| `driver-no-sandbox` | Add `--no-sandbox` flag (useful for Docker/CI) | `false` |
| `disable-notifications`| Disable browser notification popups | `true` |
| `insecure-localhost` | Accept insecure/self-signed SSL certificates | `false` |
| `allow-remote-origin` | Allow remote origins (Fixes connection issues in newer Chrome) | `true` |
| `selenium-log-level` | Logging level for the internal Selenium driver | `off` |
| **Network / Grid** | | |
| `selenium-grid` | Connect to a remote Selenium Grid hub | `false` |
| `hub-url` | The URL of the Grid Hub (Required if `selenium-grid` is true) | `""` |
| `proxy-address` | Address of the proxy server | `null` |
| `proxy-port` | Port of the proxy server | `0` |
| **Emulation** | | |
| `mobile-mode` | Enable Chrome's mobile emulation mode | `false` |
| `emulated-device` | Device profile for emulation (e.g., `iPhone12Pro`) | `iPhone12Pro` |

---

### 📱 Mobile & Desktop Configuration (Appium)

Mobile driver initialization is split into two parts: **Appium Service Connection** (defined in properties) and **Device Capabilities** (defined in JSON).

#### 1. Connection Properties (`pickleib.properties`)

These settings tell Pickleib how to connect to the Appium service (Local or Remote/Cloud).

| Property | Description | Default |
| :--- | :--- | :--- |
| **General** | | |
| `device` | **Required.** The filename of the JSON config (without `.json`) | `null` |
| `config` | Directory containing the capability JSON files | `src/test/resources/configurations` |
| `use-remote-mobile-driver`| Switch between Local Appium (`false`) and Cloud Providers (`true`) | `false` |
| **Local Service** | | |
| `address` | IP address for the local Appium server | `0.0.0.0` |
| `port` | Port for the local Appium server | `4723` |
| `appium-service-uri` | Extension for the service URL (e.g., `/wd/hub`) | `""` |
| **Remote / Cloud** | | |
| `remote-mobile-server` | The cloud provider URL (e.g., `hub-cloud.browserstack.com`) | `null` |
| `remote-mobile-username`| Username for the cloud provider | `null` |
| `remote-mobile-access-key`| Access key/Token for the cloud provider | `null` |

#### 2. Define Device Capabilities (JSON)
Create a JSON file inside the folder specified by the `config` property. The filename should match the `device` property (e.g., `InventoryApp.json`).

**Example: Conventional iOS Setup (`src/test/resources/configurations/InventoryApp.json`)**
**Note:** If the `app` path in the JSON is a valid local file path, Pickleib will automatically resolve its absolute path before sending it to the server.

```json
{
  "platformName": "iOS",
  "automationName": "XCUITest",
  "deviceName": "iPhone 14",
  "udid": "00008101-001E30590A00002E",
  "bundleId": "com.company.inventoryapp",
  "app": "src/test/resources/apps/InventoryApp.app",
  "platformVersion": "16.2",
  "noReset": true,
  "autoAcceptAlerts": true
}
```

When you initialize `PickleibAppiumDriver`, it will looks for `InventoryApp.json` in the configurations folder, parse these capabilities, and start the Appium service on port `4723`.

---

## 💻 Local Development

This repository includes a sample test website for you to practice against.

1.  **Start the Local Server:**
    ```shell
    docker-compose up --build -d
    ```

2.  **Access the Site:**
    👉 **[http://localhost:7457](http://localhost:7457)**

---

### Start a New Project

To create a compatible Cucumber project from scratch:

```shell
mvn archetype:generate                      \
"-DarchetypeGroupId=io.cucumber"            \
"-DarchetypeArtifactId=cucumber-archetype"  \
"-DarchetypeVersion=6.10.4"                 \
"-DgroupId=example"                         \
"-DartifactId=my-project"                  \
"-Dpackage=example"                        \
"-Dversion=1.0.0-SNAPSHOT"                 \
"-DinteractiveMode=false"
```

### ⚠️ Important: ArtifactId Change

As of version **2.0.1**, the Artifact ID has changed to lowercase.
* Old: `Pickleib`
* New: **`pickleib`**
