# Pickleib

[![Maven Central](https://img.shields.io/maven-central/v/io.github.umutayb/Pickleib?color=brightgreen&label=Pickleib)](https://mvnrepository.com/artifact/io.github.umutayb/Pickleib/latest)

### Description

Pickleib is a utility library for software automation projects. It helps you design and run tests with Selenium WebDriver in a simple and efficient way. It provides a set of utilities and ready to go driver setup that provide great convenience for designing test automations.

Some features and benefits of using Pickleib are:
* **Easy configuration**: You can set up your test environment with minimal code! Pickleib supports different browsers, drivers, timeouts, etc.
* **Utilities**: Pickleib has various utilities that can help you interact with WebElements in a consistent/non-flaky way. Check basic interaction methods such as click, fill, scroll, or more advanced utilities like element state, element attribute verifications. Find these at `WebUtilities.java`
* **Page Object Model**: Pickleib can be use the Page Object Model pattern to organize your web elements and actions in separate classes. Pickleib provides classes and methods to simplify this process.
* **API support**: Pickleib has built in api capabilities that enable defining calls to endpoints, model request & response bodies as well as verifying response details, testing frontend interactions against backends
* **Emails**: It has email capabilities that enable sending or receiving emails directly in the framework. This feature is further supported by utilities that acquire email HTML and test email design, content and structure using the web driver.
* **Database**: Pickleib provides database connection capabilities with JDBC. Database utilities can be used to establish an SQL connection, pass queries and map the results.
* **Data Layer**: Built in data layer desting capabilities allow verifying specific events or data stored in the data layer.

### Installation

To use Pickleib in your Maven project, add the following dependency to your pom.xml file:
```xml
<dependency>
    <groupId>com.github.umutayb</groupId>
    <artifactId>pickleib</artifactId>
    <version>1.x.x</version>
</dependency>
```

To use Pickleib in your Gradle project, add the following dependency to your build.gradle file:
```
dependencies {
    implementation 'com.github.umutayb:picklelib:1.x.x'
}

```
Project can also be cloned and built into the target framework;
```shell
mvn clean package -DbuildDirectory=/Users/{user}/Web-Automation-Sample-Cucumber/lib
```
There, the imported jar file should be added as a dependency in `pom.xml file of that project:
```xml
    <!-- Framework -->
        <dependency>
            <groupId>bora</groupId>
            <artifactId>Pickleib-1.x.x.jar</artifactId>
            <version>1.x.x</version>
            <systemPath>${project.basedir}/lib/Pickleib-1.x.x.jar</systemPath>
            <scope>system</scope>
            <type>jar</type>
        </dependency>
```

After updating your project, the quickstart library is ready to use. 
___
### Usage

The quickstart library consists of many utility methods and a ready to use web driver, database connection & api handling.
It is compatible with page object model design. The infrastructure allows easy initialization of elements by initializing them
within a constructor inside the **WebUtilities** class. In order to initialize elements inside a page class, all it takes is
to extend the **WebUtilities** class. This also extends the **Driver** class, allowing usage of driver inside page classes.

#### Step 1: Create a pages package
Implement page objects, add elements (use @FindBy annotation) & page methods. _**Remember** extending **WebUtilities** class, 
 initializing all elements within the page class._

````java
import utils.WebUtilities;

public class HomePage extends WebUtilities {...}
```` 

#### Step 2: Create a steps package
Create page step classes, instantiate page classes, create step definitions & access page methods within these step 
 definitions as:
 ````java
public class HomePageSteps {
    
    HomePage homePage = new HomePage();

    @Given("Click category card named {}")
    public void clickCategoryCard(String cardName) {
        homePage.clickCategoryCardNamed(cardName);
    }
 }
 ````
**Alternatively**, use the reflection steps found in **PickleibSteps** class.
>
>Create an ObjectRepository class, instantiate all page objects in it;
>
>```java
>public class ObjectRepository {
>
>    HomePage homePage = new HomePage();
>
>}
>```
>Then input a new instance of ObjectRepository object to PickleibSteps methods;
>```java
>import steps.PickleibSteps;
>import utils.driver.Driver;
>
>public class CommonSteps extends PickleibSteps {
>    @Given("If present, click the {} on the {}")
>    public void clickIfPresent(String buttonName, String pageName){
>        log.new Info("Clicking " +
>                highlighted(BLUE, buttonName) +
>                highlighted(GRAY," on the ") +
>                highlighted(BLUE, pageName) +
>                highlighted(GRAY, ", if present...")
>        );
>        pageName = strUtils.firstLetterDeCapped(pageName);
>        try {
>            WebElement element = getElementFromPage(buttonName, pageName, new ObjectRepository());
>            if (elementIs(element, ElementState.DISPLAYED)) clickElement(element, true);
>        }
>        catch (WebDriverException ignored){log.new Warning("The " + buttonName + " was not present");}
>    }
>}    
>```
>
>If using cucumber, set **@Before** & **@After** steps as:
>
>```java
>import utils.driver.Driver;
>
>public class CommonSteps {
>    @Before
>    public void start() {
>        Driver.initialize();
>    }
>
>    @After
>    public void kill(Scenario scenario) {
>        Driver.terminate();
>    }
>}    
>```

Use your reflection step to interact with the element;
```gherkin
@TestEnv @Web-UI @SCN-Click-If-Present
Scenario: Test cookie accept button 
  * Navigate to the test page
  * If present, click the cookieAcceptButton on the HomePage
```

Set up your test runner
```java
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/java/features"},
        plugin = {"json:target/reports/Cucumber.json"},
        glue = {"steps"},
        publish = true
)
public class TestRunner {

    @BeforeClass
    public static void initialSequence(){...}

    @AfterClass
    public static void finalSequence(){...}
}
```

 This will initialize the driver before each run, and kill it after each scenario is done. 

#### Execution
In order to execute a specific feature file on a given browser, use:
```shell
mvn clean test -q -Dcucumber.filter.tags="@TestEnv and @SCN-Click-If-Present" -Dbrowser=chrome
```

###### It is recommended to use Pickleib as designed in **Web-Automation-Smaple-Cucumber** project
To create a cucumber project from scratch instead, run the following command in your command line:
````shell
mvn archetype:generate                      \
"-DarchetypeGroupId=io.cucumber"            \
"-DarchetypeArtifactId=cucumber-archetype"  \
"-DarchetypeVersion=6.10.4"                 \
"-DgroupId=hellocucumber"                   \
"-DartifactId=hellocucumber"                \
"-Dpackage=hellocucumber"                   \
"-Dversion=1.0.0-SNAPSHOT"                  \
"-DinteractiveMode=false"
````
