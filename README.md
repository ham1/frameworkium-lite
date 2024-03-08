# Frameworkium Lite

![CI](https://github.com/ham1/frameworkium-lite/actions/workflows/ci.yaml/badge.svg)
[![codecov](https://codecov.io/gh/ham1/frameworkium-lite/branch/main/graph/badge.svg?token=07Bjy2ePfw)](https://codecov.io/gh/ham1/frameworkium-lite)

A Framework for writing maintainable Selenium and REST API tests in Java.

To use this you will need the following in your `pom.xml`

```xml
  <repositories>
    <!-- Enables us to fetch dependencies from github -->
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>com.github.ham1</groupId>
      <artifactId>frameworkium-lite</artifactId>
      <version>4.7.5</version>
    </dependency>
  </dependencies>
```

## Junit 5

We've moved to Junit 5, so you'll need migrate the following:
* `@Test` on every test method
* `@BeforeTest` -> `@BeforeEach`
* `@BeforeClass` -> `@BeforeAll`
* groups -> `@Tag` (can use at class and method level)
* Depends on methods ->
`@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` then 
`@Order(1)` and `@Order(2)` etc. on test methods - 
or just combine them into one test method
* `SkipException` -> `assumeTrue(false, "reason")`
* parameterized tests with the `@Parameters` or `@DataProvider` annotations.
-> `@ParameterizedTest` along with sources like
`@ValueSource`, `@CsvSource`, `@CsvFileSource,` `@MethodSource,` etc.
* TestNG creates a new instance of the test class for every test method by default.
JUnit 5 operates differently by default, creating a single test instance per test class.
You can adjust this behavior in JUnit 5 with
`@TestInstance(TestInstance.Lifecycle.PER_CLASS)` or
`@TestInstance(TestInstance.Lifecycle.PER_METHOD)`.

## Frameworkium Lite vs Frameworkium Core

Forked from https://github.com/Frameworkium/frameworkium-core

Incorporates a forked and updated version of
https://github.com/yandex-qatools/htmlelements
