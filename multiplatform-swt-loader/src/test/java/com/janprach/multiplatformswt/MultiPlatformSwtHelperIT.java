package com.janprach.multiplatformswt;

import com.janprach.multiplatformswt.loader.MultiPlatformSwtHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class MultiPlatformSwtHelperIT {
	private static final String SOME_SWT_CLASS_NAME = "org.eclipse.swt.SWT";

	@Test
	public void importSwtJarUninitializedSystemClassLoaderTest() {
		Assertions.assertThrows(ClassNotFoundException.class, () -> {
			ClassLoader.getSystemClassLoader().loadClass(SOME_SWT_CLASS_NAME);
		});
	}

	@Test
	public void importSwtJarUninitializedURLClassLoaderTest() {
		Assertions.assertThrows(ClassNotFoundException.class, () -> {
			final URL swtJarUrl = new URL("file:/non-existing-path");
			try (final URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{swtJarUrl})) {
				urlClassLoader.loadClass(SOME_SWT_CLASS_NAME);
			}
		});
	}

	@Test
	public void importSwtJarInitializedURLClassLoaderTest() throws ClassNotFoundException, IOException {
		final URL swtJarUrl = MultiPlatformSwtHelper.getSwtPlatformDependentJarFileUrl();
		try (final URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{swtJarUrl})) {
			Assertions.assertNotNull(urlClassLoader.loadClass(SOME_SWT_CLASS_NAME));
		}
	}
}
