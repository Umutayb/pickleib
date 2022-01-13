# Quickstart Library

### How To Get Started:

First, the library should be exported into an empty automation project. There are two ways of doing this.
### First Way: 

[![](https://jitpack.io/v/Umutayb/Pickleib.svg)](https://jitpack.io/#Umutayb/Pickleib)

The dependency can be acquired by adding Jitpack repository into the pom.xml, as well as the dependency for the library as:
```xml
<dependencies>
    <!-- Framework -->
        <dependency>
            <groupId>com.github.Umutayb</groupId>
            <artifactId>Pickleib</artifactId>
            <version>0.5.1</version>
        </dependency>   
</dependencies>
        
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
### Second Way:
```shell
mvn clean package -DbuildDirectory=directory/to/project/lib
```
For instance:
```shell
mvn clean package -DbuildDirectory=/Users/Umut/Github/Web-Automation-Sample-Cucumber/lib
```
There, the imported jar file should be added as a dependency in pom.xml file of that project:
```xml
    <!-- Framework -->
        <dependency>
            <groupId>bora</groupId>
            <artifactId>POM-Framework-0.0.2.jar</artifactId>
            <version>0.0.2</version>
            <systemPath>${project.basedir}/lib/POM-FRAMEWORK-0.0.2.jar</systemPath>
            <scope>system</scope>
            <type>jar</type>
        </dependency>
```

After updating your project, the quickstart library is ready to use. 
___
### How To Use:

The quickstart library consists of many utility methods and a ready to use built in selenium grid infrastructure,
compatible with page object model design. The infrastructure allows easy initialization of elements by initializing them
within a constructor inside the **Utilities** class. In order to initialize elements inside a page class, all it takes is
to extend the **Utilities** class. This also extends the **Driver** class, allowing usage of driver inside page classes.

#### Step 1: Create a pages package
>Create page classes, add elements (use @FindBy annotation) & page methods. _**Remember** extending **Utilities** class, 
> initializing all elements within the page class._
>````java
> public class HomePage extends Utilities {...}
>```` 

#### Step 2: Create a steps package
>Create page step classes, instantiate page classes, create step definitions & access page methods within these step 
> definitions as:
> ````java
> public class HomePageSteps {
> 
>    HomePage homePage = new HomePage();
>
>    @Given("Click category card named {}")
>    public void clickCategoryCard(String cardName) {
>        homePage.clickCategoryCardNamed(cardName);
>    }
> }
> ````
>Set cucumber @Before & @After steps as:
> ````java
>    Initialize driverManager = new Initialize();
>
>    @Before
>    public void start(){driverManager.init();}
>
>    @After
>    public void kill(Scenario scenario){driverManager.kill(scenario);}
>````
> This will initialize the driver before each run, and kill it after each scenario is done. It will also
> capture a ss if the scenario fails, indicating scenario name and failed step info.

#### Step 3: Create a features package
>Create _**.feature**_ files, create your scenarios using the steps you have implemented in ***Step 2***.

#### Step 4: Execute your tests
>###### Selenium Grid needs to be running first, turn on Docker, then in project directory start Selenium Grid & Nodes by using the following command:
>````shell
>docker-compose up -d
>````
>###### 
>The library allows the browser type to be designated on runtime, just pass:
> ````shell
> -Dbrowser=browserName
> ````
> For instance:
>````
> -Dbrowser=firefox
>````
>Chrome, Firefox, Opera, Edge & Safari are supported. 
>In order to use this feature, please add the following plugin & property to your pom.xml:

```xml
    <properties>
        <browser>Chrome</browser>
        <name>${project.name}</name>
    </properties>
___

    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>properties-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
            <execution>
                <phase>generate-resources</phase>
                    <goals>
                        <goal>write-project-properties</goal>
                    </goals>
                    <configuration>
                        <outputFile>${project.build.outputDirectory}/properties-from-pom.properties</outputFile>
                    </configuration>
            </execution>
        </executions>
    </plugin>
```
___ 
>It is recommended to use ***Cucumber JVM Parallel Plugin*** which allows you to execute tests simultaneously on parallel.
> If you would like to do that, add plugin:

```xml
          
            <plugin>
                <groupId>com.github.temyers</groupId>
                <artifactId>cucumber-jvm-parallel-plugin</artifactId>
                <version>4.2.0</version>
                <executions>
                    <execution>
                        <id>generateRunners</id>
                        <phase>generate-test-sources</phase>
                        <goals>
                            <goal>generateRunners</goal>
                        </goals>
                        <configuration>
                            <glue>steps</glue>
                            <featuresDirectory>src/test/java/features</featuresDirectory>
                            <cucumberOutputDir>target/cucumber-parallel</cucumberOutputDir>
                            <plugins>
                                <plugin>
                                    <name>testng</name>
                                </plugin>
                            </plugins>
                            <monochrome>false</monochrome>
                            <useTestNG>true</useTestNG>
                            <namingScheme>simple</namingScheme>
                            <!-- The class naming pattern to use.  Only required/used if naming scheme is 'pattern'.-->
                            <namingPattern>Parallel{c}IT</namingPattern>
                            <!-- One of [SCENARIO, FEATURE]. SCENARIO generates one runner per scenario.  FEATURE generates a runner per feature. -->
                            <parallelScheme>SCENARIO</parallelScheme>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Maven Surefire Plugin (This generates runner classes using the automatically generated test suites [.xml files]) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${maven.surefire.version}</version>
                <configuration>
                    <parallel>methods</parallel>
                    <useUnlimitedThreads>true</useUnlimitedThreads>
                    <forkCount>10</forkCount>
                    <reuseForks>true</reuseForks>
                    <includes>
                        <include>**/Parallel*IT.class</include>
                    </includes>
                </configuration>
            </plugin>
```
 
#### Example execution command:
>In order to execute a specific feature file in a specific browser, use:
> ```shell
>mvn test -Dcucumber.options="src/test/java/features/Explore.feature" -Dbrowser=chrome
> ```

#### To create a cucumber project:
>Run the following command:
>````shell
>mvn archetype:generate                      \
>"-DarchetypeGroupId=io.cucumber"            \
>"-DarchetypeArtifactId=cucumber-archetype"  \
>"-DarchetypeVersion=6.10.4"                 \
>"-DgroupId=hellocucumber"                   \
>"-DartifactId=hellocucumber"                \
>"-Dpackage=hellocucumber"                   \
>"-Dversion=1.0.0-SNAPSHOT"                  \
>"-DinteractiveMode=false"
>````
