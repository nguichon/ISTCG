package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import NewClient.ClientCardTemplate;
import NewClient.ClientCardTemplateManager;
import NewClient.ClientCardTemplate.CardRenderSize;
import Shared.CardTypes;

public class BattleField extends GameCardStorage {
	private static final int WIDTH = ClientCardTemplate.CardRenderSize.SMALL.getWidth();
	private static final int HEIGHT = ClientCardTemplate.CardRenderSize.SMALL.getHeight();
	
	private static final int DESIRED_SPACING = 5;
	
	public BattleField(Composite parent, int style, GameV2 host) {
		super( parent, style, host );
	}

	@Override
	public void AddCard( ClientGameCardInstance cgci ) {
		if( ClientCardTemplateManager.get().GetClientCardTemplate( cgci.GetTemplateID() ).getCardType() == CardTypes.COMMAND_UNIT ) {
			//cgci.ChangeSize( CardRenderSize.MEDIUM );
		}
		super.AddCard( cgci );
	}
	
	@Override
	protected void OptimizeLayout() {
		Control[] items = getChildren();
		if(items.length>0){
			try{
				Rectangle area = getClientArea();
				int columns = area.width/CardRenderSize.MEDIUM.getWidth();
				int rows = area.height/CardRenderSize.MEDIUM.getHeight();
				int count=0;
				for(int i=0;i<rows;i++){
					for(int j=0;j<columns;j++,count++){
						items[count].setBounds(area.x+10+(j*100), area.y+10+(i*100), CardRenderSize.SMALL.getWidth(), CardRenderSize.SMALL.getHeight());
						
					}
				}
			} catch(Exception e){ //do nothing
			
			}
		}
		redraw();
	}
}
