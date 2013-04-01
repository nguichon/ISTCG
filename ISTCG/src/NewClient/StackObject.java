package NewClient;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import NewClient.ClientCardTemplate.CardRenderSize;

import NewClient.ClientCardTemplateManager;
import NewClient.ClientMain;
import NewClient.Game;
import OldClient.CardTemplateManager;
import Shared.StatBlock;

public class StackObject extends Canvas {

	String name;
	String[] description;
	ArrayList<String> stats;
	CardRenderSize size;
	CardRenderSize lastSize;
	ClientMain main;
	ClientCardTemplate template;
	String id;
	PaintListener pl;
	Rectangle lastBounds;
	Composite lastParent;
	String parentCard = "";
	String targetCard = "";
	String ability = "";
	Game game;
	String text = "";
	public enum GameZone {
		HAND,FIELD,STACK,GRAVEYARD,VIEWER,UNKNOWN;
		
	}
	GameZone zone;
	public StackObject(Composite parent, int style, ClientMain main, String ID, Game game) {
		super(parent, style);
		this.main=main;
		this.id=ID;
		this.game=game;
		this.lastParent=parent;
		size = CardRenderSize.SMALL;
		zone = GameZone.STACK;
		lastBounds = this.getBounds();
		pl = new PaintListener(){
			@Override
			public void paintControl(PaintEvent e) {
				Render(e.gc);
				
			}
		};
		this.addPaintListener(pl);
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				cardDClicked();
				
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				cardClicked(arg0);
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
				
			}
			
		});
		//this.setTemplate(parentCard);
	}
	
	public void setParentCard(String cardID){
		this.parentCard=cardID;
	}
	public String getParentCard(){
		
		return parentCard;
	}
	public void setTargetCard(String cardID){
		
		this.targetCard=cardID;
	}
	public String getTargetCard(){
		return targetCard;
	}

	public void setAbilityIndex(int i){
		ability = String.valueOf(i);
	}
	public void setAbilityIndex(String i){
		ability = i;
	}
	public String getAbilityIndex(){
		return ability;
	}
	
	public void setLastParent(Composite parent){
		lastParent=parent;
	}
	
	public void cardDClicked(){
		switch(size){
		
		//case SMALL:
		//	break;
			
		//case MEDIUM:
		//	break;
		case LARGE:
			size = lastSize;
			redraw();
			break;
		default:
			lastSize = size;
			size = CardRenderSize.LARGE;
			redraw();
			break;
		}
	}
	
	public void cardClicked(MouseEvent mouse){
		//get current size
		switch(size){
		case SMALL:
			lastBounds = getBounds();
			game.setViewer(this.getID());
			break;
			
		case MEDIUM:
			break;
		case LARGE:
			
			//play card or what have you
			break;
		default: redraw(); break;
		}
	}
	
	
	public void setTemplate(String ID){
		template = ClientCardTemplateManager.get().GetClientCardTemplate(Integer.valueOf(ID));
		this.redraw();
	}
	public ArrayList<String> getStats(){
		return stats;
	}
	public String getStat(String stat){
		if(stats!=null){
			return (stats.get(StatBlock.StatType.valueOf(stat).ordinal())!=null?stats.get(StatBlock.StatType.valueOf(stat).ordinal()):template.getStat(stat));
		}
		return "-1";
	}
	public void setStat(String stat, String val){
		if(stats!=null){
			stats.add(StatBlock.StatType.valueOf(stat).ordinal(),val);
		}
	}
	public void setStat(String stat, int val){
		if(stats!=null){
			stats.add(StatBlock.StatType.valueOf(stat).ordinal(),String.valueOf(val));
		}
	}
	public void setStat(StatBlock stat, String val){
		if(stats!=null){
			stats.add(stat.m_Type.ordinal(),val);
		}
	}
	public void setStat(StatBlock stat, int val){
		if(stats!=null){
			stats.add(stat.m_Type.ordinal(),String.valueOf(val));
		}
	}
	public void setStat(StatBlock stat){
		if(stats!=null){
			stats.add(stat.m_Type.ordinal(),String.valueOf(stat.m_Value));
		}
	}
	public void setStatBlock(ArrayList<StatBlock> stats){
		if(stats!=null){
			for(StatBlock s : stats){
				setStat(s);
			}
		}
	}
	public void Render(GC gc){
		if(template!=null) {
			template.Render(gc, size, getStatBlock(),0);
			//RenderStats(gc);
		} else {
			ClientCardTemplate.RenderBlack( gc, size, getStatBlock());
		}
	}
	
	public ArrayList<StatBlock> getStatBlock(){
		ArrayList<StatBlock> tmp = new ArrayList<StatBlock>();
		tmp.add(StatBlock.StatType.ATTACK.ordinal(),new StatBlock(StatBlock.StatType.ATTACK,Integer.valueOf(getStat("ATTACK"))));
		tmp.add(StatBlock.StatType.DEFENSE.ordinal(),new StatBlock(StatBlock.StatType.DEFENSE,Integer.valueOf(getStat("DEFENSE"))));
		tmp.add(StatBlock.StatType.POWER.ordinal(),new StatBlock(StatBlock.StatType.POWER,Integer.valueOf(getStat("POWER"))));
		tmp.add(StatBlock.StatType.STRUCTURE.ordinal(),new StatBlock(StatBlock.StatType.STRUCTURE,Integer.valueOf(getStat("STRUCTURE"))));
		tmp.add(StatBlock.StatType.HARD_POINTS.ordinal(),new StatBlock(StatBlock.StatType.HARD_POINTS,Integer.valueOf(getStat("HARD_POINTS"))));
		tmp.add(StatBlock.StatType.DELAY.ordinal(),new StatBlock(StatBlock.StatType.DELAY,Integer.valueOf(getStat("DELAY"))));
		tmp.add(StatBlock.StatType.METAL.ordinal(),new StatBlock(StatBlock.StatType.METAL,Integer.valueOf(getStat("METAL"))));
		tmp.add(StatBlock.StatType.ENERGY.ordinal(),new StatBlock(StatBlock.StatType.ENERGY,Integer.valueOf(getStat("ENERGY"))));
		tmp.add(StatBlock.StatType.TECH.ordinal(),new StatBlock(StatBlock.StatType.TECH,Integer.valueOf(getStat("TECH"))));
		return tmp;
	}
	public String getName(){
		return name;
	}
	public String getID(){
		return id;
	}
	public CardRenderSize getRenderSize(){
		return size;
	}
	public void setRenderSize(ClientCardTemplate.CardRenderSize size){
		this.size=size;
	}

	public void setText(String text) {
		this.text=text;
	}
	public String getText(){
		return text;
	}

}
