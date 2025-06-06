package com.github.jendap.multiplatformswt.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class SwtHelloWorld {
    public static void main(final String[] args) {
        var display = new Display();
        var shell = new Shell(display);
        shell.setText("Multiplatform Swt Example");
        shell.setLayout(new GridLayout());

        var button = new Button(shell, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        button.setText("Hello world!");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });
        shell.setDefaultButton(button);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}
