package NewClient;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import Client.ImageManager;

public class StoreItem extends Canvas {
	
	private int i_Cost;
	private String i_Des;
	private String i_Name;
	private String i_ID;
	private String BGImage;
	
	StoreUI storeUI;
	//TODO enum category's
	
	
	public StoreItem(Composite parent, int style, String id, int cost, String name, String decription, StoreUI store) {
		super(parent, style);
		SetCost(cost);
		SetDes(decription);
		SetName(name);
		SetID(id);
		storeUI = store;
		BGImage = i_Name+".png";
		//TODO SetBGImage():  ?? Image Manager? needs size 300x400 (same as card size)
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				cardUClicked();
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	public void Render(GC targetGC, Point size){
		targetGC.drawImage(ImageManager.get().GetImage(BGImage), 0, 0, 300, 400, 0, 0, size.x, size.y);
		
	}
	
	private void cardUClicked(){
		storeUI.SetCur(this);
	}
	
	//Get&Set
	//i_Cost
	public int GetCost(){
		return i_Cost;
	}
	public void SetCost(int cost){
		i_Cost = cost;
	}
	//i_Des
	public String GetDes(){
		return i_Des;
	}
	public void SetDes(String change){
		i_Des = change;
	}
	//i_Name
	public String GetName(){
		return i_Name;
	}
	public void SetName(String st){
		i_Name = st;
	}
	//i_ID
	public String GetID(){
		return i_ID;
	}
	public void SetID(String id){
		i_ID = id;
	}
	//i_BGImage
	public Image GetBGImage(){ 
		//TODO  return BGImage;
		return null;
	}
	public void SetBGImage(GC target){
		//TODO BG image
		//i_BGImage = new Image(target, i_Name +".png");
	}
	

}
