package com.github.jendap.multiplatformswt.build;

import com.github.jendap.multiplatformswt.runtime.MultiPlatformJarLauncher;
import com.github.jendap.multiplatformswt.runtime.MultiPlatformJarNaming;
import com.github.jendap.multiplatformswt.runtime.NotCurrentPlatformPredicate;
import org.springframework.boot.loader.tools.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;

public class MultiPlatformJarLayout extends Layouts.Jar implements CustomLoaderLayout {
    @Override
    public String getLauncherClassName() {
        return MultiPlatformJarLauncher.class.getName();
    }

    @Override
    public void writeLoadedClasses(final LoaderClassesWriter writer) throws IOException {
        // copy the original spring bootloader classes
        writer.writeLoaderClasses();
        // copy our own multiplatform-swt runtime classes
        writeLoadedClass(writer, MultiPlatformJarLauncher.class);
        writeLoadedClass(writer, NotCurrentPlatformPredicate.class);
        writeLoadedClass(writer, MultiPlatformJarNaming.class);
        // add platform specific eclipse jars from dependencies section of spring-boot-maven-plugin
        writePlatformEclipseJars(writer);
    }

    private void writeLoadedClass(final LoaderClassesWriter writer, final Class<?> clazz) throws IOException {
        final String entryName = clazz.getName().replace('.', '/') + ".class";
        writer.writeEntry(entryName, clazz.getResourceAsStream(clazz.getSimpleName() + ".class"));
    }

    private void writePlatformEclipseJars(LoaderClassesWriter writer) throws IOException {
        final MultiPlatformJarNaming eclipseJarHelper = new MultiPlatformJarNaming();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (final Enumeration<URL> metaInfUrls = classLoader.getResources("META-INF"); metaInfUrls.hasMoreElements(); ) {
            final URL metaInfUrl = metaInfUrls.nextElement();
            final String jarUrl = metaInfUrl.getFile().replaceAll("!/META-INF$", "");
            if (eclipseJarHelper.isPlatformEclipseJar(jarUrl)) {
//                System.err.println(">>>> writePlatformEclipseJars: " + jarUrl);
                final String jarFileName = Paths.get(jarUrl).getFileName().toString();
                final String libraryLocation = this.getLibraryLocation(jarFileName, LibraryScope.RUNTIME);
//                writer.writeEntry(Paths.get(libraryLocation, jarFileName).toString(), new URL(jarUrl).openStream());
                if (writer instanceof JarWriter) {
                    final JarWriter jarWriter = (JarWriter) writer;
                    final Library library = new Library(Paths.get(jarUrl.replace("file:", "")).toFile(), LibraryScope.RUNTIME);
                    jarWriter.writeNestedLibrary(libraryLocation, library);
                }
            }
        }
    }
}
