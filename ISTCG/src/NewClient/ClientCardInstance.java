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

public class ClientCardInstance extends Canvas {

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
	Game game;
	String owner = "";
	String controller = "";
	int m_DamageTaken = 0;
	public enum GameZone {
		HAND,FIELD,STACK,GRAVEYARD,VIEWER,UNKNOWN;
		
	}
	GameZone zone;
	public ClientCardInstance(Composite parent, int style, ClientMain main, String ID, Game game) {
		super(parent, style);
		this.main=main;
		this.id=ID;
		this.game=game;
		this.lastParent=parent;
		size = CardRenderSize.SMALL;
		zone = GameZone.UNKNOWN;
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
	}
	
	public void setZone(String zone){
		this.zone = GameZone.valueOf(zone);
	}
	public void setZone(GameZone zone){
		this.zone=zone;
	}
	
	public void setLastParent(Composite parent){
		lastParent=parent;
	}
	
	public void cardDClicked(){
//		switch(size){
//		
//		//case SMALL:
//		//	break;
//			
//		//case MEDIUM:
//		//	break;
//		case LARGE:
//			size = lastSize;
//			redraw();
////			if(zone==GameZone.VIEWER){
////				setBounds(lastBounds);
////				size = CardRenderSize.SMALL;
////				setParent(this.lastParent);
////				redraw();
////			}
//			break;
//		default:
//			lastSize = size;
//			size = CardRenderSize.LARGE;
//			redraw();
//			break;
//		}
		game.playCard(this.id);
		game.setAssistText(this.controller);
	}
	
	public void cardClicked(MouseEvent mouse){
		//get current size
		switch(size){
		case SMALL:
			//Enlarge the card
			lastBounds = getBounds();
			//Rectangle curBounds = getBounds();
			//setBounds(curBounds.x,curBounds.y-64,CardRenderSize.LARGE.getWidth(),CardRenderSize.LARGE.getHeight());
//			size = CardRenderSize.LARGE;
//			setParent(game.group);
//			game.group.layout();
//			redraw();
//			game.group.layout();
			game.setViewer(this.getID());
			game.vcard=this.getID();
			break;
			
		case MEDIUM:
			break;
		case LARGE:
			String aM = "ABILITY;" + game.getID() + ";" + id + ";"; 
			if(mouse.x >= 235){
				if (35<= mouse.y && mouse.y < 60){
					if (stats.size() >= 1){
						main.sendData(aM + "0");
					}
				}
				if (60<= mouse.y && mouse.y < 85){
					if (stats.size() >= 2){
						main.sendData(aM + "1");
					}
				}
				if (85<= mouse.y && mouse.y < 110){
					if (stats.size() >= 3){
						main.sendData(aM + "2");
					}
				}
				if (110<= mouse.y && mouse.y < 135){
					if (stats.size() >= 4){
						main.sendData(aM + "3");
					}
				}
				if (135<= mouse.y && mouse.y < 160){
					if (stats.size() >= 5){
						main.sendData(aM + "4");
					}
				}
				if (160<= mouse.y && mouse.y < 185){
					if (stats.size() >= 6){
						main.sendData(aM + "5");
					}
				}
				if (185<= mouse.y && mouse.y < 210){
					if (stats.size() >= 7){
						main.sendData(aM + "6");
					}
				}
				if (235<= mouse.y && mouse.y < 260){
					if (stats.size() >= 8){
						main.sendData(aM + "7");
					}
				}
			}
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
			template.Render(gc, size, getStatBlock(), m_DamageTaken);
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

	public void setOwner(String owner) {
		
		this.owner=owner;
	}
	public String getOwner(){
		return owner;
	}
	public void setController(String controller) {
		this.controller=controller;
	}
	public String getController(){
		return controller;
	}

}
