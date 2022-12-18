package com.github.jendap.multiplatformswt.runtime;

import org.springframework.boot.loader.archive.Archive;

import java.util.function.Predicate;

public class NotCurrentPlatformPredicate implements Predicate<Archive> {
    final MultiPlatformJarNaming eclipseJarHelper = new MultiPlatformJarNaming();

    @Override
    public boolean test(final Archive archive) {
        final String jar = archive.toString();
        return eclipseJarHelper.isPlatformEclipseJar(jar) && !eclipseJarHelper.isCurrentPlatformEclipseJar(jar);
    }
}
