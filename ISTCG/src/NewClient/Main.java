package NewClient;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class Main extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Main(Composite parent, int style) {
		super(parent, style);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(386, 10, 64, 244);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setBounds(386, 262, 64, 28);
		btnNewButton.setText("Send");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
