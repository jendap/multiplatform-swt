package com.github.jendap.multiplatformswt.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

// Don't forget to switch `main.class` property in pom.xml to point to this class before running:
// $ mvn package -Pmultiplatform && java -jar multiplatform-swt-example/target/*.jar
public class SwtAdvancedApp {
    private final static Logger logger = Logger.getLogger(SwtAdvancedApp.class.getName());

    public static void main(final String[] args) {
        final SwtAdvancedApp swtHelloWorld = new SwtAdvancedApp();
        swtHelloWorld.run();
    }

    public void run() {
        Display.setAppName(this.getClass().getName());

        final Display display = new Display();
        final Shell shell = new Shell(display);

        try {
            drawBrowser(shell);

            shell.open();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "Error running the app.", e);
            final MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.ERROR);
            messageBox.setMessage("Error running the app." + "\n\n" + exceptionToString(e));
            messageBox.setText("Error");
            messageBox.open();
//            System.exit(-1);
        } finally {
            display.dispose();
        }
    }

    private void drawBrowser(final Shell shell) {
        shell.setText("Multiplatform Swt Browser Example");
        shell.setLayout(new GridLayout());

        final Browser browser = new Browser(shell, SWT.NONE);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        browser.setText("<html><body>Hello world!</body></html>");
        browser.setUrl("https://letmegooglethat.com/?q=meow");
    }

    private String exceptionToString(final Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
