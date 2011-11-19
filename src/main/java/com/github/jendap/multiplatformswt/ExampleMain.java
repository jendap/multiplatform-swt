package com.github.jendap.multiplatformswt;

// BEWARE: Do not import anything from or.eclispe.swt in your main class!!!

public class ExampleMain {
	public static void main(final String[] args) {
		// add the correct swt jar to the classpath
		final MultiPlatformSwtHelper multiPlatformSwtHelper = new MultiPlatformSwtHelper();
		multiPlatformSwtHelper.addSwtPlatformDependentJarURLToSystemClassLoader();
		
		// run your own main or whatever you want
		final SwtHelloWorld swtHelloWorld = new SwtHelloWorld();
		swtHelloWorld.swtHelloWorld();
	}
}
