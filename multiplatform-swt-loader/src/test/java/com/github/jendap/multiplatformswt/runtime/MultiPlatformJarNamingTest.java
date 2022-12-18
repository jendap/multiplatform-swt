package com.github.jendap.multiplatformswt.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiPlatformJarNamingTest {
    @Test
    public void testIsPlatformEclipseJar() {
        final MultiPlatformJarNaming naming = new MultiPlatformJarNaming();
        assertFalse(naming.isPlatformEclipseJar("org.eclipse.swt-3.122.0.jar"));
        assertTrue(naming.isPlatformEclipseJar("org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar"));
        assertTrue(naming.isPlatformEclipseJar("/BOOT-INF/lib/org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar!/"));
        assertTrue(naming.isPlatformEclipseJar("jar:file:org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar!/META-INF"));
        assertTrue(naming.isPlatformEclipseJar(
                "jar:file:/tmp/foo-1.0.jar!/BOOT-INF/lib/org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar!/"));
        assertTrue(naming.isPlatformEclipseJar("org.eclipse.core.filesystem.macosx-1.3.400-javadoc.jar.asc"));
    }

    @Test
    public void testIsCurrentPlatformEclipseJar() {
        final MultiPlatformJarNaming linux64 = new MultiPlatformJarNaming("Linux", "amd64");
        assertFalse(linux64.isCurrentPlatformEclipseJar("org.eclipse.swt-3.122.0.jar"));
        assertTrue(linux64.isCurrentPlatformEclipseJar("org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar"));
        final MultiPlatformJarNaming linux64arm = new MultiPlatformJarNaming("Linux", "aarch64");
        assertFalse(linux64arm.isCurrentPlatformEclipseJar("org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar"));
        assertTrue(linux64arm.isCurrentPlatformEclipseJar("org.eclipse.swt.gtk.linux.aarch64-3.122.0.jar"));
        final MultiPlatformJarNaming windows64 = new MultiPlatformJarNaming("Windows 10", "amd64");
        assertFalse(windows64.isCurrentPlatformEclipseJar("org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar"));
        assertTrue(windows64.isCurrentPlatformEclipseJar("org.eclipse.swt.win32.win32.x86_64-3.122.0.jar"));
        final MultiPlatformJarNaming macosx32 = new MultiPlatformJarNaming("Mac Os X", "x86");
        assertFalse(macosx32.isCurrentPlatformEclipseJar("org.eclipse.swt.gtk.linux.x86_64-3.122.0.jar"));
        assertFalse(macosx32.isCurrentPlatformEclipseJar("org.eclipse.swt.cocoa.macosx.x86-3.122.0.jar"));
        assertTrue(macosx32.isCurrentPlatformEclipseJar("org.eclipse.swt.cocoa.macosx-3.122.0.jar"));
    }
}
