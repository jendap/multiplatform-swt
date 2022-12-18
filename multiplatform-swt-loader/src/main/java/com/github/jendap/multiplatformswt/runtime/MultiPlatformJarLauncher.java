package com.github.jendap.multiplatformswt.runtime;

import org.springframework.boot.loader.archive.Archive;

import java.util.List;

public class MultiPlatformJarLauncher extends org.springframework.boot.loader.JarLauncher {
    @Override
    protected boolean isPostProcessingClassPathArchives() {
        return true;
    }

    @Override
    protected void postProcessClassPathArchives(final List<Archive> archives) {
        archives.removeIf(new NotCurrentPlatformPredicate());
    }

    public static void main(String[] args) throws Exception {
        new MultiPlatformJarLauncher().launch(args);
    }
}
