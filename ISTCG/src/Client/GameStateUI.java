package Client;

import java.util.Vector;

import org.eclipse.swt.widgets.Control;

public abstract class GameStateUI {
	protected Vector<Control> m_UIObjects;
	protected ClientMain m_Host;
	
	public abstract void Disable();
	public abstract void Enable();
	
	public abstract void HandleMessage( String[] inputs );
}
