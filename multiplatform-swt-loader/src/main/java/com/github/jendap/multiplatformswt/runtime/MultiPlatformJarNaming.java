package com.github.jendap.multiplatformswt.runtime;

import java.util.Locale;
import java.util.regex.Pattern;

public class MultiPlatformJarNaming {
    private static final String SUPPORTED_OS_ARCH_SUFFIXES = "(aix|hpux|linux|macosx|solaris|win32)(.[^\\.\\-\\!]*)?";
    private final Pattern platformEclipseJarPattern;
    private final Pattern currentPlatformEclipseJarPattern;

    public MultiPlatformJarNaming() {
        this(System.getProperty("os.name"), System.getProperty("os.arch"));
    }

    public MultiPlatformJarNaming(final String osName, final String osArch) {
        this.platformEclipseJarPattern = createPlatformEclipseJarPattern(SUPPORTED_OS_ARCH_SUFFIXES);
        final String suffix = toEclipseOsArchSuffix(osName, osArch);
        this.currentPlatformEclipseJarPattern = createPlatformEclipseJarPattern(suffix);
    }

    private static Pattern createPlatformEclipseJarPattern(final String osArchSuffix) {
        final String regex = ".*org\\.eclipse\\.[^\\-!]*" + osArchSuffix + "-[^!]*\\.jar.*";
        return Pattern.compile(regex);
    }

    public boolean isPlatformEclipseJar(final String classPathEntry) {
        return this.platformEclipseJarPattern.matcher(classPathEntry).matches();
    }

    public boolean isCurrentPlatformEclipseJar(final String classPathEntry) {
        return this.currentPlatformEclipseJarPattern.matcher(classPathEntry).matches();
    }

    private String toEclipseOsArchSuffix(final String osName, final String osArch) {
        final String osSuffix = toEclipseOsSuffix(osName);
        final String archSuffix = toEclipseArchSuffix(osArch);
        if (osSuffix.equals("macosx") && archSuffix.equals("x86")) {
            return osSuffix;
        } else {
            return osSuffix + "." + archSuffix;
        }
    }

    private String toEclipseOsSuffix(final String osName) {
        final String normalizedOsName = osName.toLowerCase(Locale.ENGLISH);
        if (normalizedOsName.startsWith("linux")) {
            return "linux";
        } else if (normalizedOsName.startsWith("mac") || normalizedOsName.startsWith("osx")) {
            return "macosx";
        } else if (normalizedOsName.startsWith("win")) {
            return "win32";
        } else if (normalizedOsName.startsWith("aix")) {
            return "aix";
        } else if (normalizedOsName.startsWith("sun")) {
            return "solaris";
        } else if (normalizedOsName.startsWith("hpux") || normalizedOsName.startsWith("hp-ux")) {
            return "hpux";
        } else {
            throw new RuntimeException("Unsupported 'os.name': \"" + osName + "\"!");
        }
    }

    // from: https://github.com/trustin/os-maven-plugin/blob/master/src/main/java/kr/motd/maven/os/Detector.java
    private String toEclipseArchSuffix(final String osArch) {
        final String normalizedOsArch = osArch.toLowerCase(Locale.ENGLISH);
        if (normalizedOsArch.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
            return "x86_64";
        } else if (normalizedOsArch.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
            return "x86";
        } else if (normalizedOsArch.matches("^(aarch64)$")) {
            return "aarch64";
        } else if (normalizedOsArch.matches("^(ia64w?|itanium64)$")) {
            return "ia64";
        } else if (normalizedOsArch.matches("^(ppc|ppc32)$")) {
            return "ppc";
        } else if (normalizedOsArch.matches("^(ppc64)$")) {
            return "ppc64";
        } else if (normalizedOsArch.matches("^(ppc64le)$")) {
            return "ppc64le";
        } else if (normalizedOsArch.matches("^(s390)$")) {
            return "s390";
        } else if (normalizedOsArch.matches("^(s390x)$")) {
            return "s390x";
        } else {
            throw new RuntimeException("Unsupported 'os.arch': \"" + osArch + "\"!");
        }
    }
}
