package NewClient;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class GameSession extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GameSession(Composite parent, int style) {
		super(parent, style);
		
		Label lblGraveyard = new Label(this, SWT.NONE);
		lblGraveyard.setBounds(10, 452, 59, 14);
		lblGraveyard.setText("Graveyard");
		
		Label lblDeck = new Label(this, SWT.NONE);
		lblDeck.setBounds(74, 452, 59, 14);
		lblDeck.setText("Deck");
		
		Label lblFoe = new Label(this, SWT.NONE);
		lblFoe.setBounds(10, 10, 59, 14);
		lblFoe.setText("Foe");
		
		Label lblYou = new Label(this, SWT.NONE);
		lblYou.setBounds(0, 432, 59, 14);
		lblYou.setText("You");
		
		Button btnNext = new Button(this, SWT.NONE);
		btnNext.setBounds(843, 452, 94, 28);
		btnNext.setText("Next");
		
		Button btnEndTurn = new Button(this, SWT.NONE);
		btnEndTurn.setEnabled(false);
		btnEndTurn.setBounds(843, 486, 94, 28);
		btnEndTurn.setText("End Turn");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
