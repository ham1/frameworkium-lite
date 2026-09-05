# Frameworkium Lite

![CI](https://github.com/ham1/frameworkium-lite/actions/workflows/ci.yaml/badge.svg)

A Framework for writing maintainable Selenium and REST API tests in Java.

Requires Java 21.

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
      <version>4.11.0</version>
    </dependency>
  </dependencies>
```

Forked from https://github.com/Frameworkium/frameworkium-core

Incorporates a forked and updated version of
https://github.com/yandex-qatools/htmlelements
