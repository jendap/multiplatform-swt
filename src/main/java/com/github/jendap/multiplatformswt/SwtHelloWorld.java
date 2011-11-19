package com.github.jendap.multiplatformswt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtHelloWorld {
	public void swtHelloWorld() {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		final Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Hello world!");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		shell.setDefaultButton(ok);
		shell.setLayout(new RowLayout());
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
