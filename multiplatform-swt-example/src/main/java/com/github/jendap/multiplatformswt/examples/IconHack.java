package com.github.jendap.multiplatformswt.examples;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/// This is a hack for linux. You (very likely) don't want to create and/or copy *.desktop and *.svg files
/// like this. It is fragile. It is silent, unexpected and error-prone (file overwrites, absolute paths, ...).
public class IconHack {
    private final static Logger logger = Logger.getLogger(IconHack.class.getName());

    public static void writeIconFile(Class<?> clazz) {
        var fileName = clazz.getName() + ".svg";
        var targetPath = Path.of(System.getProperty("user.home"), ".local", "share", "icons", fileName);
        copyResource(clazz.getClassLoader(), fileName, targetPath, null);
    }

    public static void writeDesktopFile(Class<?> clazz) {
        var fileName = clazz.getName() + ".desktop";
        var targetPath = Path.of(System.getProperty("user.home"), ".local", "share", "applications", fileName);
        copyResource(clazz.getClassLoader(), fileName, targetPath, "rwxr-xr-x");
    }

    /// This is really dangerous. It overwrites existing desktop file. Only developer may use this.
    /// For example, it works with `mvn package -Pmultiplatform && java -jar multiplatform-swt-example/target/*.jar`.
    public static void dangerouslyOverwriteDesktopFileTotalHack(Class<?> clazz) {
        var appName = clazz.getName();
        var homeDir = System.getProperty("user.home");
        var svgFilePath = Path.of(homeDir, ".local", "share", "icons", appName + ".svg");
        var desktopFilePath = Path.of(homeDir, ".local", "share", "applications", appName + ".desktop");

        try {
            var desktopFileContent =
                    "[Desktop Entry]\n" +
                            "Name=Multiplatform Swt Browser Example\n" +
                            "Comment=Example (Eclipse SWT) application packaged by maven into one big multiplatform jar.\n" +
                            "Exec=" + ProcessHandle.current().info().commandLine().orElseThrow() + "\n" +
                            "Icon=" + svgFilePath.toAbsolutePath() + "\n" +
                            "Path=" + System.getProperty("user.dir") + "\n" +
                            "Terminal=false\n" +
                            "Type=Application\n" +
                            "Categories=Utility;\n" +
                            "StartupWMClass=" + appName + "\n";

            Files.createDirectories(desktopFilePath.getParent());
            Files.writeString(desktopFilePath, desktopFileContent);
            Files.setPosixFilePermissions(desktopFilePath, PosixFilePermissions.fromString("rwxr-xr-x"));
//            desktopFilePath.toFile().deleteOnExit();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error copying file.", e);
        }
    }

    private static void copyResource(ClassLoader classLoader, String resourceName, Path targetPath, String perms) {
        if (!System.getProperty("os.name").toLowerCase(Locale.ROOT).equals("linux")) {
            logger.fine("Unsupported operating system. Only on linux by this hack.");
        } else {
            try {
                try (var inputStream = classLoader.getResourceAsStream(resourceName)) {
                    if (inputStream == null) {
                        logger.fine("Resource " + resourceName + " not found on classpath.");
                    } else if (Files.exists(targetPath)) {
                        logger.fine("File " + targetPath + " already exists.");
                    } else {
                        Files.createDirectories(targetPath.getParent());
                        Files.copy(inputStream, targetPath);
                        if (perms != null) {
                            Files.setPosixFilePermissions(targetPath, PosixFilePermissions.fromString(perms));
                        }
                        logger.fine("File " + targetPath + " created.");
//                        targetPath.toFile().deleteOnExit();
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error copying file.", e);
            }
        }
    }
}
