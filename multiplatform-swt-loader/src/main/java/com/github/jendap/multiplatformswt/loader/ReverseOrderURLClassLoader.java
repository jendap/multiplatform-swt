package com.github.jendap.multiplatformswt.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class ReverseOrderURLClassLoader extends URLClassLoader {
	public ReverseOrderURLClassLoader(final URL[] urls) {
		super(urls);
	}

	public ReverseOrderURLClassLoader(final URL[] urls,
			final ClassLoader parentClassLoader) {
		super(urls, parentClassLoader);
	}

	@Override
	protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			// First, check if the class has already been loaded
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				// invoke findClass in order to find the class.
				try {
					c = findClass(name);
				} catch (ClassNotFoundException e) {
					// ClassNotFoundException thrown if class not found
					// from the non-null parent class loader
				}
			}
			if (c == null) {
				// If still not found, then try call parent
				c = getParent().loadClass(name);
			}
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
	}
}
