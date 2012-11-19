Multiplatform SWT
==================

This project show how to make multiplatform swt project using maven without osgi. Linux, macosx
and windows are supported in both 32-bit and 64 bit versions.

You are advised to use tycho to build swt based projects! Not this approach. That way you can reuse
all the other eclipse plugins. But this approach is sometimes better.


Usage
-----

* Copy and paste pretty much the whole pom.xml into your pom.xml.
  - Copy the properties and change `rsrc.main.class` to point to your intended main
  - Copy the dependencies including scopes and classifiers
  - Copy `assembly` plugin with its configuration (or put the Rsrc-Main-Class into jar plugin)
  - Copy `repositories` section
  - Copy `profiles` section
* Congratulations! You've done it.

* BTW: build it with "mvn package"


How it works
------------

It create one jar with your application. Set the main class to be `MultiPlatformSwtHelper`.
For customizations just copy main method from `MultiPlatformSwtHelper` to your own new class
and change as needed. That main method is supposed to add the right jar file to the classpath.
The assembly plugin is needed again. Because we push your main, i.e. `rsrc.main.class`, into
a jar file. Without it the system classloader would load your main which would be in root
of the final jar. The classloader of your main would be a system classloader. And that does
not see dynamically added jars into a new classloader.

Complicated. Read about class loading. It can be done different ways. None perfect. 

BTW: At development time the swt jar files are not switched by this main. Java compiler
(and IDEs) would not see SWT at compile time. Therefore the switching is done using maven
profiles. Correct profile is activated by platform (build in functionality in maven).


Limitations
-----------

* You have to copy and paste so much because there's no support for mixins in maven yet.
* Your first class with main method (`MultiPlatformSwtHelper` or your customized) can't
  import any swt nor call any class that needs swt because the swt is nowhere to be found
  on the classpath by system classloader before that main is run.
* You may have a problem if you're using different classloaders than standard system one.
  In that case I hope you know what you're doing ;-)


Eclipse
-------

* Got to `project properties`
* Select `maven`
* Add your profile name to `Active Maven Profiles` (i.e. add one of linux32, linux64,
  macosx32, macosx64, win32 or win64)


What failed to make it easier
-----------------------------

* You can't simply unpack all the classes from all the swt jars into one because there are
  conflicts between them. (class files with the same name are different for different platforms)
* You need the right platform dependency in your development environment. That's why there are
  all those maven profiles. Profiles are not propagated when you include the loader as dependency.
  Mixins are needed for that. Hopefully they come with maven 3.1. Nobody knows when that will be.
* It would be nice to use p2 repository resolver without tycho and osgi. The question is whether
  it's possible.


Building platform dependent stuff
---------------------------------

It's a very bad idea. Building multiplatform project with platform dependent stuff? Bad.

Anyway you may need it. For example you may have some ridiculous feature for your enterprise
customer. You need some technology like OLE for it. Of course you don't need it as a developer.
You may be using something like jenkins on linux for ci and release builds. If that's your case
use manual profile selection to override the automatic platform selection, use:
  `-P win32,!win64,!linux32,!linux64,!macosx32,!macosx64`

It will work. You will build one package that will run on your dev machine - linux/windows.
However when you try to call or just load a class with that platform dependent feature (like OLE)
you'll get ClassNotFoundException.
