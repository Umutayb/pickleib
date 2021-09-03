#Quickstart Library
###For POM & Cucumber Projects
___
###How To Get Started:

First, the library should be exported into an empty automation project by using:
```
mvn clean package -DbuildDirectory=directory/to/project/lib
```
For instance:
```
mvn clean package -DbuildDirectory=/Users/Umut/Github/Web-Automation-Sample-Cucumber/lib
```
There, the imported jar file should be added as a dependency in pom.xml file of that project:
```
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
###How To Use:

The quickstart library consists of many utility methods and a ready to use built in selenium grid infrastructure,
compatible with page object model design. The infrastructure allows easy initialization of elements by initializing them
within a constructor inside the **Utilities** class. In order to initialize elements inside a page class, all it takes is
to extend the **Utilities** class. This also extends the **Driver** class, allowing usage of driver inside page classes.

####Step 1: Create a pages package
>Create page classes, add elements (use @FindBy annotation) & page methods. _**Remember** extending **Utilities** class, 
> initializing all elements within the page class._

####Step 2: Create a steps package
>Create page step classes, instantiate page classes, create step definitions & access page methods within these step 
> definitions.

####Step 3: Create a features package
>Create _**.feature**_ files, create your scenarios using the steps you have implemented in ***Step 2***.

####Step 4: Execute your tests
>The library allows the browser type to be designated on runtime, just pass:
> ````
> -Dbrowser=browserName
> ````
> For instance:
>````
> -Dbrowser=firefox
>````
>Chrome, Firefox, Opera, Edge & Safari are supported. 
>In order to use this feature, please add the following plugin & property to your pom.xml:


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
___ 
>It is recommended to use ***Cucumber JVM Parallel plugin*** which allows you to execute tests simultaneously on parallel.
> If you would like to do that, add plugin:

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
####Example execution command:
>In order to execute a specific feature file in a specific browser, use:
>```
>mvn test -Dcucumber.options="src/test/java/features/Explore.feature" -Dbrowser=chrome
>```
