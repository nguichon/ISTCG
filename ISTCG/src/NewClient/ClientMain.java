package NewClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ClientMain {

	private Display display = null;
	private Login Login = null;
	private Lobby Lobby = null;
	private Shell shell = null;
	private Composite composite = null;
	public ClientMain(){
		
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		Login = new Login(shell, SWT.NONE,this );
		Lobby = new Lobby(shell, SWT.NONE,this);
		composite = Login;
		composite.setBounds(shell.getBounds());
		shell.pack();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public Point getSize(){
		return display.getActiveShell().getSize();
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		new ClientMain();
	}
	public Rectangle getBounds(){
		return shell.getBounds();
	}
	public void login(String user, String password){
		/*
		 * For now just move to next UI
		 * 
		 */
		composite.dispose();
		
		
		
		shell.setBounds(display.getPrimaryMonitor().getBounds());
		composite = new Lobby(shell, SWT.NONE,this);
		Rectangle r = shell.getBounds();
		//Rectangle t = new Rectangle((r.width-composite.getBounds().width)/2,r.width/2,(r.height-composite.getBounds().height)/2,r.height/2);
		composite.setBounds(r);
		//shell.pack();
		//shell.open();
		//shell.layout();
	}
}
