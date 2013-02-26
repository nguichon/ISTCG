package NewClient;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

public class ClientMain {

	public ClientMain(){
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		Composite composite = new Login(shell, SWT.NONE,this );
		composite.setBounds(0, 0, 450, 278);
		shell.pack();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		new ClientMain();
	}
	public void login(String user, String password){
		
	}
}
