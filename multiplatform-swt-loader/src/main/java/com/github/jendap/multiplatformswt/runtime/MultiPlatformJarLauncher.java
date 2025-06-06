package com.github.jendap.multiplatformswt.runtime;

import org.springframework.boot.loader.launch.Archive;

public class MultiPlatformJarLauncher extends org.springframework.boot.loader.launch.JarLauncher {
    final MultiPlatformJarNaming eclipseJarHelper = new MultiPlatformJarNaming();

    public MultiPlatformJarLauncher() throws Exception {
    }

    @Override
    protected boolean isIncludedOnClassPath(Archive.Entry entry) {
        final String jar = entry.toString();
        return !eclipseJarHelper.isPlatformEclipseJar(jar) || eclipseJarHelper.isCurrentPlatformEclipseJar(jar);
    }

    public static void main(String[] args) throws Exception {
        new MultiPlatformJarLauncher().launch(args);
    }
}
