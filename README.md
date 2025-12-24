# Pickleib

[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/pickleib?color=brightgreen\&label=pickleib)](https://mvnrepository.com/artifact/io.github.umutayb/pickleib/latest)

### Template Project

To see Pickleib in action, and to use it as a **no code solution**, check out the
👉 [test-automation-template](https://github.com/Umutayb/test-automation-template)

---

### ArtifactId Change!

As of version **2.0.1**, Pickleib has a new artifactId.
Artifact id was changed from **`Pickleib`** → **`pickleib`**

---

## Installation

To use Pickleib in your Maven project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.umutayb</groupId>
    <artifactId>pickleib</artifactId>
    <version>2.0.5</version>
</dependency>
```

After updating your project, Pickleib is ready to use 🚀

---

## Description

Pickleib is a **comprehensive automation utility library** designed to streamline software testing across multiple platforms.
It leverages **Selenium** and **Appium** to provide a robust framework for:

* 🌐 Web UI automation
* 📱 Mobile UI automation
* 🖥️ Desktop UI automation
* 🔌 API automation
* 🗄️ Database validation
* 📧 Email verification
* 📊 Data-layer validation

Pickleib simplifies test design and execution by offering **ready-to-use driver management**, **powerful utilities**, and **flexible element acquisition strategies**.

---

### Key Features

* **Effortless Setup**
  Configure your test environment with minimal code. Pickleib supports multiple browsers, drivers, platforms, and device emulations out-of-the-box.

* **Rich Interaction Utilities**
  A powerful set of interaction methods for stable and non-flaky tests:

    * Click, fill, scroll, hover
    * Element state & attribute verification
      Available via `WebUtilities` and `PlatformUtilities`.

* **Flexible Element Acquisition**

    * Classic **Page Object Model (POM)** by extending:

        * `PickleibPageObject` (Web)
        * `PickleibScreenObject` (Mobile/Desktop)
    * **Reflection-based steps** for dynamic interaction
    * **JSON-based Page Object Repository** using `PageObjectJson` for fully decoupled element definitions

* **Cross-Functional Testing**

    * API testing via **Retrofit**
    * Database interactions using **JDBC**
    * Advanced email sending, receiving & HTML verification

* **Data Layer Validation**
  Built-in capabilities to verify data layer events, values, and structures directly from your tests.

---

## Usage

Pickleib is designed for **quick integration**.
It provides ready-to-use **drivers**, **database connections**, and **API clients**, while remaining fully compatible with **Page Object Model** design.

---

### Driver Setup (Cucumber)

Manage driver lifecycle using Cucumber hooks.

```java
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import pickleib.web.driver.PickleibWebDriver;
import pickleib.platform.driver.PickleibAppiumDriver;

public class Hooks {

    @Before
    public void start() {
        // Web tests
        PickleibWebDriver.initialize();

        // Mobile/Desktop tests
        // PickleibAppiumDriver.initialize();
    }

    @After
    public void kill(Scenario scenario) {
        // Web tests
        PickleibWebDriver.terminate();

        // Mobile/Desktop tests
        // PickleibAppiumDriver.terminate();
    }
}
```

---

### Page Objects

Create page classes by extending `PickleibPageObject` (Web) or `PickleibScreenObject` (Mobile/Desktop).
This automatically initializes elements via `PageFactory` and exposes all interaction utilities.

```java
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pickleib.web.PickleibPageObject;
import java.util.List;

public class HomePage extends PickleibPageObject {

    @FindBy(id = "category-card")
    public List<WebElement> categories;

    public void selectCategory(String categoryName){
        clickElement(
                acquireNamedElementAmongst(categories, categoryName, "Home Page")
        );
    }
}
```

---

### Step Definitions

#### 1️⃣ Direct Method

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

#### 2️⃣ Reflection Method (Dynamic Steps)

Use Pickleib’s reflection utilities to interact with any element dynamically.

**Object Repository**

```java
import pages.*;
import pickleib.utilities.interfaces.repository.PageObjectRepository;

public class ObjectRepository implements PageObjectRepository {
    public HomePage homePage = new HomePage();
    public FormsPage formsPage;
}
```

**Dynamic Step Definitions**

```java
import common.ObjectRepository;
import pickleib.utilities.steps.PageObjectDesign;
import org.openqa.selenium.WebElement;

public class CommonSteps extends PageObjectDesign<ObjectRepository> {

    public CommonSteps() {
        super(ObjectRepository.class);
    }

    @When("I click the {string} on the {string} page")
    public void clickTheButton(String buttonName, String pageName) {
        log.info("Clicking the " + buttonName + " on the " + pageName);
        WebElement button = objectRepository.acquireElementFromPage(buttonName, pageName);
        webInteractions.clickElement(button);
    }
}
```
or for page object Json design:

```java
import common.ObjectRepository;
import org.openqa.selenium.WebElement;

public class CommonSteps extends PageJsonStepUtilities {

    public CommonSteps() {
        super(
                FileUtilities.Json.parseJsonFile("src/test/resources/page-repository.json"),
                Hooks.initialiseAppiumDriver,
                Hooks.initialiseBrowser
        );
    }

    @When("I click the {string} on the {string} page")
    public void clickTheButton(String buttonName, String pageName) {
        log.info("Clicking the " + buttonName + " on the " + pageName);
        WebElement button = objectRepository.acquireElementFromPage(buttonName, pageName);
        webInteractions.clickElement(button);
    }
    
    ...
}
```

**Usage in Feature File**

```gherkin
Scenario: Dynamically click an element
When I click the "submitButton" on the "FormsPage" page
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

---

If you want, I can also:

* 🔄 Produce a **migration guide** (old → new APIs)
* ✂️ Create a **short README** version
* 📘 Split this into **Docs / Wiki-ready sections**
