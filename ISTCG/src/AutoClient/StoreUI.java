package AutoClient;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class StoreUI extends GameStateUI {
	public StoreUI( Shell client, AutoClientMain main ) {
		m_Host = main;
		m_UIObjects = new Vector<Control>();
		final Label storeTitle = new Label(client, SWT.READ_ONLY);
		storeTitle.setAlignment(SWT.CENTER);
		storeTitle.setBounds(250,0,100,50);
		storeTitle.setText("ISTCG Store");
		/*
		 * Add more stuff here later, will need to implement points and shit.
		 * 
		 */
		
		
		m_UIObjects.add(storeTitle);
		for( Control w : m_UIObjects ) {
			w.setVisible(false);
		}
	}
	public int getWidth(){
		return this.getWidth();
	}
	public int getHeight(){
		return this.getHeight();
	}
	@Override
	public void Disable() {
		for( Control w : m_UIObjects ) {
			w.setVisible(false);
		}
	}

	@Override
	public void Enable() {
		for( Control w : m_UIObjects ) {
			w.setVisible(true);
		}
	}
	@Override
	public void HandleMessage(String[] inputs) {
		
	}
}
