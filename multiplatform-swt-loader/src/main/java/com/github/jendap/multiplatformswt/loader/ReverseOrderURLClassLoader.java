package com.github.jendap.multiplatformswt.loader;

import java.net.URL;
import java.net.URLClassLoader;

final public class ReverseOrderURLClassLoader extends URLClassLoader {
	static {
		ClassLoader.registerAsParallelCapable();
	}

	public ReverseOrderURLClassLoader(final URL[] urls) {
		super(urls);
	}

	public ReverseOrderURLClassLoader(final URL[] urls, final ClassLoader parentClassLoader) {
		super(urls, parentClassLoader);
	}

	@Override
	public final Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
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
			if (c == null && this.getParent() != null) {
				try {
					c = this.getParent().loadClass(name);
				} catch (ClassNotFoundException e) {
					// ClassNotFoundException thrown if class not found
					// from the non-null parent class loader
				}
			}
			if (c == null) {
				// If still not found, then invoke findClass in order
				// to find the class.
				c = findClass(name);
			}
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
	}
}
