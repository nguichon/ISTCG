package NewClient;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;

public class StoreUI extends Composite {

	GC gc;
	ClientMain main;
	String ID="";
	String eID="";
	TabItem tab;
	int bal;
	Label lbl_STORE;
	Label lblBal;
	//Label lblPdisplay;
	Label lbl_pv_Text;
	Button btnBuy;
	Button btnBack;
	Point backSize;
	Point buySize;
	Point backLoc;
	Point buyLoc;
	Point itemPos1;
	Point pv_ImgLoc;
	Point pv_TextLoc;
	Point pv_TextSize;
	Point nameLoc;
	Point imgLarge;
	Point imgSmall;
	int rColX;//right column indent
	int lEdgeX;//left edge border
	int tEdgeY;//top edge border
	int tRowY;//top row y
	int bRowY;//bottom row y
	
	//org.eclipse.swt.graphics.Color red = new Color(this, 255, 0, 0);
	
	String curCost;
	StoreItem curItem;
	ArrayList<StoreItem> items;
	
	
	
	public StoreUI(Composite parent, int style, final ClientMain main, TabItem tab) {
		super(parent, style);
		this.main=main;
		this.tab=tab;
		//TODO get client balance
		bal = 42;//temp test
		
		
		//Border arrangements
		rColX = parent.getBounds().width  -310;
		lEdgeX = 10;
		tEdgeY = 10;
		tRowY = 50;
		bRowY = parent.getBounds().height - 40;
		
		//TODO points
		imgLarge = new Point(300, 400);
		imgSmall = new Point(180, 240);
		itemPos1 = new Point(lEdgeX, tRowY);
		backLoc = new Point(lEdgeX,tEdgeY);
		backSize = new Point(60, 30);
		buySize = new Point(60, 30);
		buyLoc = new Point(rColX, bRowY);
		pv_ImgLoc = new Point(rColX, tRowY);
		pv_TextLoc = new Point(rColX, tRowY + 410);
		pv_TextSize = new Point(300, bRowY - 10);
		nameLoc = new Point((parent.getBounds().width)/2, tEdgeY);
		
		
		//TODO labels
		//Client name
		lbl_STORE = new Label(this, SWT.NONE);
		lbl_STORE.setLocation(nameLoc);
		lbl_STORE.setText("Store");
		
		//TODO preview
		//preview text
		curItem = new StoreItem(this, SWT.None, "0", 0, "NULL", "NULL", this);
		lbl_pv_Text = new Label(this, SWT.WRAP);
		lbl_pv_Text.setLocation(pv_TextLoc);
		lbl_pv_Text.setSize(pv_TextSize);
		lbl_pv_Text.setText(curItem.GetDes());
		//Preview Image
		curItem.Render(gc, imgLarge);
		curItem.setLocation(pv_ImgLoc);
		
		
		//TODO Load and dispaly items
		//use imgSmall for size
		items = LoadStore();
		
		
		
		setLayout(new FormLayout());//?
		
		//Back Button
		//TODO format
		btnBack = new Button(this, SWT.NONE);
		FormData fd_btnBack = new FormData();
		btnBack.setLayoutData(fd_btnBack);
		btnBack.setLocation(backLoc);
		btnBack.setSize(backSize);
		btnBack.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Go to previous category OR undo search
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		btnBack.setText("Back");
		
		
		//Buy Button
		//TODO format
		btnBuy = new Button(this, SWT.NONE);
		FormData fd_btnBuy = new FormData();
		//btnBuy.setBackground(red);
		btnBuy.setLayoutData(fd_btnBuy);
		btnBuy.setLocation(buyLoc);
		btnBuy.setSize(buySize);
		btnBuy.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Confirmation();
				// TODO go to confirmation box
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		btnBuy.setText("BUY : " + curCost);
		
		// TODO Auto-generated constructor stub
		
		
	}
	public void Confirmation(){
		//TODO
	}
	public ArrayList<StoreItem> LoadStore(){
		ArrayList<StoreItem> merch = null;
		//TODO search
		
		return merch;
		
	}
	//TODO get&set
	//Current item
	public StoreItem GetCur(){
		return curItem;
	}
	public void SetCur(StoreItem it){
		curItem = it;
		
		
		redraw();
	}
	
}
