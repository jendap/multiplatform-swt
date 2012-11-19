package com.github.jendap.multiplatformswt.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.internal.jarinjarloader.JIJConstants;
import org.eclipse.jdt.internal.jarinjarloader.RsrcURLStreamHandlerFactory;

public class MultiPlatformSwtHelper {
	private final static Logger LOGGER = Logger.getLogger(MultiPlatformSwtHelper.class.getName());
	private static final String MULTIPLATFORM_SWT_PROPERTIES = "META-INF/multiplatform-swt.properties";

	public ClassLoader getSwtPlatformDependentJarInJarClassLoader() {
		try {
			final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
			URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(parentClassLoader));
			final String swtJarPath = getSwtPlatformDependentJarPath();
			final URL swtRsrcUrl = this.convertPathToRsrcUrl(swtJarPath);
			final List<String> classPathElements = new ArrayList<String>();
			final String redirectedClassPath = this.extractValueFromManifest(JIJConstants.REDIRECTED_CLASS_PATH_MANIFEST_NAME);
			if (redirectedClassPath != null) {
				classPathElements.addAll(Arrays.asList(redirectedClassPath.split(" ")));
			}
			final List<URL> rsrcUrls = new ArrayList<URL>();
			rsrcUrls.addAll(Arrays.asList(((URLClassLoader) parentClassLoader).getURLs()));
			rsrcUrls.add(swtRsrcUrl);
			for (final String classPathElement : classPathElements) {
				rsrcUrls.add(this.convertPathToRsrcUrl(classPathElement));
			}
			return new ReverseOrderURLClassLoader(rsrcUrls.toArray(new URL[] {}), parentClassLoader);
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private URL convertPathToRsrcUrl(final String rsrcPath) throws MalformedURLException {
		final URL rsrcUrl;
		if (rsrcPath.endsWith(JIJConstants.PATH_SEPARATOR)) {
			rsrcUrl = new URL(JIJConstants.INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath);
		} else {
			rsrcUrl = new URL(JIJConstants.JAR_INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath
					+ JIJConstants.JAR_INTERNAL_SEPARATOR);
		}
		return rsrcUrl;
	}

	public void addSwtPlatformDependentJarURLToSystemClassLoader() {
		this.addSwtPlatformDependentJarURLToURLClassLoader((URLClassLoader) ClassLoader.getSystemClassLoader());
	}

	public void addSwtPlatformDependentJarURLToURLClassLoader(final URLClassLoader classLoader) {
		try {
			final URL swtFileUrl = this.getSwtPlatformDependentJarFileUrl();
			LOGGER.log(Level.FINE, "Adding swt jar file " + swtFileUrl + " on classpath.");

			final Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addUrlMethod.setAccessible(true);
			addUrlMethod.invoke(classLoader, swtFileUrl);
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to add the swt jar", e);
		}
	}

	public String getSwtPlatformDependentJarPath() {
		return this.getLibDirectory() + "/" + "org.eclipse.swt" + getSwtFileNameOsSuffix() +
				getSwtFileArchSuffix() + getSwtVersionSuffix() + ".jar";
	}

	public URL getSwtPlatformDependentJarFileUrl() throws MalformedURLException {
		return new URL("file:" + getSwtPlatformDependentJarPath());
	}

	public String getSwtFileArchSuffix() {
		final String osName = System.getProperty("os.name").toLowerCase();
		final String osArch = System.getProperty("os.arch").toLowerCase();
		final String swtFileNameArchSuffix;
		if (osArch.contains("64")) {
			swtFileNameArchSuffix = ".x86_64";
		} else {
			swtFileNameArchSuffix = osName.contains("mac") ? "" : ".x86";
		}
		return swtFileNameArchSuffix;
	}

	public String getSwtFileNameOsSuffix() {
		final String osName = System.getProperty("os.name").toLowerCase();
		final String swtFileNameOsPart;
		if (osName.contains("win")) {
			swtFileNameOsPart = ".win32.win32";
		} else if (osName.contains("mac")) {
			swtFileNameOsPart = ".cocoa.macosx";
		} else if (osName.contains("linux") || osName.contains("nix")) {
			swtFileNameOsPart = ".gtk.linux";
		} else {
			throw new RuntimeException("Unsupported OS name: " + osName);
		}
		return swtFileNameOsPart;
	}

	private String getLibDirectory() {
		return this.extractValueFromMultiplatformSwtProperties("lib.directory");
	}

	public String getSwtVersionSuffix() {
		final String swtVersion = this.extractValueFromMultiplatformSwtProperties("swt.version");
		return (swtVersion != null) ? "-" + swtVersion : "";
	}

	private String extractValueFromMultiplatformSwtProperties(final String key) {
		final ClassLoader classLoader = this.getClass().getClassLoader();
		final InputStream inputStream = classLoader.getResourceAsStream(MULTIPLATFORM_SWT_PROPERTIES);
		final Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (final IOException e) {
			LOGGER.log(Level.WARNING, "Unable to read " + MULTIPLATFORM_SWT_PROPERTIES, e);
		}
		return properties.getProperty(key);
	}

	private String extractValueFromManifest(final String key) {
		try {
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
			if (resources != null) {
				while (resources.hasMoreElements()) {
					try {
						final Manifest manifest = new Manifest(resources.nextElement().openStream());
						final String value = manifest.getMainAttributes().getValue(key);
						if (value != null) {
							return value;
						}
					} catch (final IOException e) {
						LOGGER.log(Level.WARNING, "Unable to read META-INF/MANIFEST.MF", e);
					}
				}
			}
		} catch (final IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to read META-INF/MANIFEST.MF", e);
		}
		return null;
	}

	public static void main(final String[] args) {
		LOGGER.addHandler(new ConsoleHandler());
		LOGGER.setLevel(Level.ALL);

		final MultiPlatformSwtHelper multiPlatformSwtHelper = new MultiPlatformSwtHelper();
		final ClassLoader classLoader = multiPlatformSwtHelper.getSwtPlatformDependentJarInJarClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			final String mainClassName = multiPlatformSwtHelper
					.extractValueFromManifest(JIJConstants.REDIRECTED_MAIN_CLASS_MANIFEST_NAME);
			System.err.println(mainClassName);
			final Class<?> mainClass = Class.forName(mainClassName, false, classLoader);
			final Method mainMethod = mainClass.getMethod("main", new Class[] { args.getClass() });
			mainMethod.invoke((Object) null, new Object[] { args });
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to load the main class or run it's main method!", e);
		}
	}
}
