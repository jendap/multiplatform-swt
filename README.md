Multi Platform SWT
==================

This project help build multiplatform swt application jar.
One jar to run on all platforms using just maven without osgi or tycho.


Try it
------

``` bash
git clone https://github.com/jendap/multiplatform-swt.git
cd multiplatform-swt
mvn package -Pmultiplatform
java -jar multiplatform-swt-example/target/*.jar
```

Usage
-----

Copy `dependecies`, `spring-boot-maven-plugin` and `profiles` section from
[swt-multiplatform-example/pom.xml](multiplatform-swt-example/pom.xml)
into your pom.xml.

It is important to keep the dependency on `multiplatform-swt-loader`
and `layoutFactory` configuration in `spring-boot-maven-plugin`.
All versions of multiplatform jar files have to be on classpath when
building the final package. The example achieve this by defining
`multiplatform` profile.


How it works
------------

This project builds on top of the great
[spring-boot-maven-plugin](http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html).
We create custom
[MultiPlatformJarLayout](multiplatform-swt-loader/src/main/java/com/github/jendap/multiplatformswt/MultiPlatformJarLayout.java)
to add our own
[MultiPlatformJarLauncher](multiplatform-swt-loader/src/main/java/com/github/jendap/multiplatformswt/MultiPlatformJarLauncher.java).
The jar launcher implements `postProcessClassPathArchives` to remove
undesirable jars from classpath.

The classpath (and the jar) contains all the dependencies.
We are adding all the platform specific variants to classpath
when building the jar. See `multiplatform` profile in
[swt-multiplatform-example/pom.xml](multiplatform-swt-example/pom.xml).

The actual logic of system properties `os.name` and `os.arch`
mapping to eclipse jar file names is in
[MultiPlatformJarNaming](multiplatform-swt-loader/src/main/java/com/github/jendap/multiplatformswt/MultiPlatformJarNaming.java).


Notes
-----

* Check other options of using swt. There used to be `tycho` project. It was modularized
  eclipse platform. It allowed to reuse all plugins of eclipse ecosystem. But that was
  some years back. There may be other recommended ways these days.
* Feel free to add `WarLauncher` and/or `PropertesLauncher` in similar fashion.
