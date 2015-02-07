Multiplatform SWT
==================

This project show how to make multiplatform swt project using maven without osgi. Linux, macosx
and windows are supported in both 32-bit and 64 bit versions.

You are advised to use tycho to build swt based projects! Not this approach. That way you can reuse
all the other eclipse plugins. But this approach is sometimes better.


Try it
------

``` bash
git clone https://github.com/jendap/multiplatform-swt.git
cd multiplatform-swt
mvn package
java -jar multiplatform-swt-example/target/multiplatform-swt-example-*.jar
```


Usage
-----

* Note that the project consists of two modules.
* First build and install the swt-multiplatform-loader module (you will use
  the swt-multiplatform-loader-VERSION-multiplatform.jar as a dependency in you project)
``` bash
mvn install
```
* Copy everything from swt-multiplatform-example/pom.xml into your pom.xml - from 'properties' down.
  (The whole file except the header - first 10 liens or so.)
  - Copy the `dependencies` including scopes and classifiers
  - Copy `maven-dependency-plugin` and `spring-boot-maven-plugin` sections with their configurations
  - Copy `repositories` section
  - Copy `profiles` section
* Change your main.class property.


How it works
------------

It create one jar with your application. See
[spring-boot-maven-plugin](http://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html)
for details. This plugin will also copy into the parent jar content of
[spring-boot-loader](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-tools/spring-boot-loader).
Custom [JarLauncher](multiplatform-swt-loader/src/main/java/org/springframework/boot/loader/JarLauncher.java)
will shadow the original `JarLauncher` from spring-boot-loader. Our version filter
out all the SWT jar files but one - the correct one for a given runtime platform.

*BEWARE*: Keep the class name and package name the same! Also update the custom
[JarLauncher](multiplatform-swt-loader/src/main/java/org/springframework/boot/loader/JarLauncher.java)
class once you update spring-boot-loader project!


Notes
-----

* Feel free to add and modify `WarLauncher` and/or `PropertesLauncher` in similar fashion.
* At development time the swt jar files are switched by maven plugins. Our customized
  `JarLauncher` kicks in spring-boot-maven-plugin builded jar file.
* Eclise
  - It works out of the box on newer Eclipse (at least on Luna)
  - Older Eclipse you may need to activate one of the profiles manually
    - Go to `project properties`
    - Select `maven`
    - Add your profile name to `Active Maven Profiles`
      (i.e. add one of linux32, linux64, macosx32, macosx64, win32 or win64)
* This project used simple rsrc loader copied out of eclipse before. It used
  reverse findClass order in custom classloader. It was awkward. `spring-boot-loader`
  is fundamentally better. If you look for the old version - see branch
  [rsrc-loader](../../tree/rsrc-loader).


#### What failed to make this easier

* You can't simply unpack all the classes from all the SWT jars into one because there are
  conflicts between them. (class files with the same name are different for different platforms)
* You need the right platform dependency in your development environment. That's why there are
  all those maven profiles. Profiles are not propagated when you include the loader as dependency.
  Mixins are needed for that. Hopefully they come with future maven versions. Nobody knows when
  that will be.
* It would be nice to use p2 repository resolver without tycho and osgi. The question is whether
  it's possible.


#### Building platform dependent stuff

It's a very bad idea. Building multiplatform project with platform dependent stuff? Bad.

Anyway you may need it. For example you may have some ridiculous feature for your enterprise
customer. You need some technology like OLE for it. Of course you don't need it as a developer.
You may be using something like jenkins on linux for ci and release builds. If that's your case
use manual profile selection to override the automatic platform selection, use:
```bash
-P win32,!win64,!linux32,!linux64,!macosx32,!macosx64
```

It will work. You will build one package that will run on your dev machine - linux/windows.
However when you try to call or just load a class with that platform dependent feature (like OLE)
you'll get ClassNotFoundException.
