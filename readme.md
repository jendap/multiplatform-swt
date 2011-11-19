Multiplatform SWT
==================

This project show how to make multiplatform swt project using maven without osgi. Linux, macosx
and windows are supported in both 32-bit and 64 bit versions.

You are advised to use tycho to build swt based projects! Not this approach. That way you can reuse
all the other eclipse plugins. But this approach is sometimes better.


Usage
-----

* Copy and paste pretty much the whole pom.xml into your pom.xml.
  - Copy the swt dependency including the "provided" scope
  - Copy `jar`, `dependency` and `assembly` plugins with their configurations
  - Copy `repositories` section
  - Copy `profiles` section
* Copy the assembly plugin configuration - `src/main/assembly/multiarch.xml`
* Have a look at `ExampleMain.java` and create you're own main class like that.
  - BEWARE: **Do not import anything from SWT!** Make it as simple as this example
    Main.java is. Call everything else from the class you call from this main. Just call:

```java
final MultiPlatformSwtHelper multiPlatformSwtHelper = new MultiPlatformSwtHelper();
multiPlatformSwtHelper.addSwtPlatformDependentJarURLToSystemClassLoader();
```

* Congratulations! You've done it.
* BTW: build it with "mvn package"


How it works
------------

It create one zip with your application. The main class calls `MultiPlatformSwtHelper` which add the
tight jar file to the classpath. At development time the swt jar files are switched using maven
profiles. They are activated by platform.

Limitations
-----------

* You have to copy and paste so much because there's no support for mixins in maven yet.
* Your main can't import any swt nor call any class that needs swt because the swt is nowhere
  to be found on the classpath until the `addSwtPlatformDependentJarURLToSystemClassLoader()` run.
* You may have a problem if you're using different classloaders than standard system one.
  In that case I hope you know what you're doing ;-)
* **Only SWT 3.6.2** is in public maven repositories right now. You can install new jars
  into your own maven repository though.


Eclipse
-------

* Got to `project properties`
* Select `maven`
* Add your profile name to `Active Maven Profiles` (i.e. add one of linux32, linux64,
  macosx32, macosx64, win32 or win64)


What failed to make it easier
-----------------------------

* You can't simply unpack all the casses from all the swt jars into one because there are
  conflicts between them. (the classfiles with the same name are different for different platforms)
* You need the right platform dependency in your development environment. So you need profiles.
  And those are not propagated if you would include this as standard dependency.
* Putting them into one jar file (jar of jars) seemed like a good idea, but:
  - You would need your own classloader. There are many of them and each seems to have some
    problems. Especially once you combine multiple classloaders in you application.
  - How would compiler load the classes from jar-in-jar swt for compile time verification?
* I don't know how to use p2 repository resolver without tycho and osgi.
* It's simple. I don't know how to make it simpler without huge effort.


Building platform dependent stuff
---------------------------------

It's a very bad idea. Building multiplatform project with platform dependent stuff? Bad.

Anyway you may need it. For example you may have some ridiculous feature for your enterprise
customer. You need the ultra cool OLE for it. Of course you don't need it as a developer.
You may be using somethink like jenkins on linux for ci and release builds. If that's your
case use manual profile selection to override the automatic platform selection, use:
  `-P win32,!win64,!linux32,!linux64,!macosx32,!macosx64`

It will work. You will build one package that will run eon your dev machine - linux/windows.
However when you try to call or just load a class with that ultra cool feature you'll get
ClassNotFoundException.
