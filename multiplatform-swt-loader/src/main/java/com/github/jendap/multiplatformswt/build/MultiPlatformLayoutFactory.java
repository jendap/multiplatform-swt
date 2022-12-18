package com.github.jendap.multiplatformswt.build;

import org.springframework.boot.loader.tools.Layout;
import org.springframework.boot.loader.tools.LayoutFactory;

import java.io.File;

public class MultiPlatformLayoutFactory implements LayoutFactory {
    @Override
    public Layout getLayout(final File source) {
        return new MultiPlatformJarLayout();
    }
}
