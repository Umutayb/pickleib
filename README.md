# Pickleib

[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/pickleib?color=brightgreen&label=pickleib)](https://mvnrepository.com/artifact/io.github.umutayb/pickleib/latest)

**Pickleib** is a comprehensive, polymorphic test automation utility library designed to streamline **Web (Selenium)**, **Mobile (Appium)**, **Desktop (Appium)**, and **API** testing.

It acts as a robust wrapper around Selenium and Appium, allowing you to write interaction-agnostic code that works across platforms. It offers a unique **"Hybrid" Page Object Model** approach, letting you choose between a classic Java implementation or a "Low-Code" JSON-based element definition.

---

## 🚀 Key Features

Pickleib simplifies test design by offering ready-to-use driver management, powerful utilities, and flexible design patterns.

* **🌐 Polymorphic Interactions:** Write tests that run on web, mobile & desktop platforms using a unified interface (`PolymorphicUtilities`).
* **🏗️ Hybrid Page Object Model:**
    * **Classic POM:** Use standard Java classes with `@FindBy` annotations.
    * **Low-Code POM:** Define your pages and selectors in a single `page-repository.json` file—no page classes required!
* **🚗 Smart Driver Management:** Automated handling of `WebDriver` and `AppiumDriver` lifecycles (Singleton pattern).
* **❤️‍🩹 Self-Healing Utilities:** Built-in retry mechanisms for `StaleElementReferenceException` and intelligent `FluentWait` synchronization.
* **🧳 Context Management:** A global `ContextStore` for sharing data between steps and configuring run-time environment variables.
* **📝 Verbose Logging:** Automatically logs interactions (e.g., "Clicking 'loginButton' on 'LoginPage'") for easier debugging.
* **🛠️ Cross-Functional Testing:**
    * 🔌 API testing via **Wasapi** (Retrofit)
    * 🗄️ Database interactions using **JDBC**
    * 📧 Advanced email sending, receiving & HTML verification
    * 📊 **Web Data Layer Validation:** Verify events, values, and structures directly.

---

### ⚠️ Important: ArtifactId Change

As of version **2.0.1**, the Artifact ID has changed to lowercase.
* Old: `Pickleib`
* New: **`pickleib`**

---

## 📦 Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.umutayb</groupId>
    <artifactId>pickleib</artifactId>
    <version>2.0.8</version>
</dependency>
```

## 🏗️ Architecture & Usage
Pickleib is designed for **quick integration**. It provides ready-to-use drivers, database connections, and API clients
while remaining fully compatible with the Page Object Model design. It allows you to structure your tests in two ways.
---

## Driver Setup (Test Hooks)

Manage the driver lifecycle using hooks.

```java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pickleib.web.driver.PickleibWebDriver;

public class Hooks {

    @BeforeEach
    public void start() {
        // Initialize Web Driver
        PickleibWebDriver.initialize();

        // OR Initialize Mobile/Desktop Driver
        // PickleibAppiumDriver.initialize();
    }

    @AfterEach
    public void kill(Scenario scenario) {
        // Terminate Web Driver
        PickleibWebDriver.terminate();

        // OR Terminate Mobile/Desktop Driver
        // PickleibAppiumDriver.terminate();
    }
}
```

## Step Definitions

### Direct Approach

Instantiate page objects and call their methods directly.

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

### Dynamic Approach
Use Pickleib’s reflection utilities to interact with any element dynamically.

### Method 1: The "Low-Code" JSON Repository (Recommended)

Define your elements in a JSON file. Pickleib will parse this file at runtime to locate elements, reducing Java boilerplate.

**1. Create `src/test/resources/page-repository.json`:**

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

**2. Initialize `PickleibSteps` in your Step Definition:**

```java
import common.ObjectRepository;
import pickleib.utilities.steps.PickleibSteps;
import org.openqa.selenium.WebElement;

public class CommonSteps extends PickleibSteps {

    public CommonSteps() {
        super("src/test/resources/page-repository.json");
    }

    @When("I click the {string} on the {string} page")
    public void clickTheButton(String buttonName, String pageName) {
        log.info("Clicking the " + buttonName + " on the " + pageName);
        WebElement button = getElementRepository().acquireElementFromPage(buttonName, pageName);
        getInteractions(button).clickElement(button);
    }
}
```

---

### Method 2: Classic Page Object Model

Use standard Java classes extending `PickleibPageObject` and register them in a central repository class.

**1. Create a Page Class:**

```java
package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;

public class LoginPage extends PickleibPageObject {
    
    @FindBy(id = "user-name")
    public WebElement usernameInput;

    @FindBy(id = "login-button")
    public WebElement loginButton;
}
```

**2. Register in an Object Repository:**

Create a class that implements `PageObjectRepository`. Declare your page classes as fields here. Pickleib uses reflection to scan this class.

```java
package common;

import pages.LoginPage;
import pickleib.utilities.interfaces.repository.PageObjectRepository;

public class ObjectRepository implements PageObjectRepository {
    
    // The framework will detect these fields via reflection
    public LoginPage loginPage;
    
    // Optional: Define environments
    public enum Environment {
        test("test-url"),
        dev("dev-url");

        final String urlKey;
        Environment(String urlKey){ this.urlKey = urlKey; }
    }
}
```

**3. Initialize `PickleibSteps` with the Class Repository:**

```java
import common.ObjectRepository;
import pickleib.utilities.steps.PickleibSteps;
import org.openqa.selenium.WebElement;

public class CommonSteps extends PickleibSteps {

    public CommonSteps() {
        super(ObjectRepository.class);
    }

    @When("I click the {string} on the {string} page")
    public void clickTheButton(String buttonName, String pageName) {
        log.info("Clicking the " + buttonName + " on the " + pageName);
        WebElement button = getElementRepository().acquireElementFromPage(buttonName, pageName);
        getInteractions(button).clickElement(button);
    }
}
```
**Usage in a Feature File**

```gherkin
Background: Context user
  * Navigate to the test page

@Web-UI @Scenario-1
Scenario: Click interactions
  * I click the submitButton on the FormsPage page
```

---

### Test Runner

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

---

### Execution

Run tests via Maven, filtering by tags and browser:

```shell
mvn clean test -Dcucumber.filter.tags="@Regression and @Web" -Dbrowser=chrome
```

---

### Creating a Cucumber Project from Scratch

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

---

### Local Development

This repository includes a sample test website.

Run it locally using Docker:

```shell
docker-compose up --build -d
```

The test website will be available at:
👉 **[http://localhost:8080](http://localhost:8080)**
