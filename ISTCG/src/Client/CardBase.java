package Client;

import org.eclipse.swt.graphics.Image;

public class CardBase extends Shared.CardBase {
	private Image m_CardImage;
	private String m_CardName;
	
	public Image GetImage() { return m_CardImage; }
	public String GetName() { return m_CardName; }
	
	@Override
	public void LoadCard(String path) {
		// TODO Auto-generated method stub
		
	}
}