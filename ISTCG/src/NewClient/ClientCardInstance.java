package NewClient;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import NewClient.ClientCardTemplate.CardRenderSize;
import Shared.StatBlock;

public class ClientCardInstance extends Canvas {

	String name;
	String[] description;
	ArrayList<String> stats;
	CardRenderSize size;
	ClientMain main;
	ClientCardTemplate template;
	String id;
	public ClientCardInstance(Composite parent, int style, ClientMain main, String ID) {
		super(parent, style);
		this.main=main;
		this.id=ID;
		size = CardRenderSize.SMALL;
		this.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e) {
				Render(e.gc);
				
			}
			
		});
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
			template.Render(gc, size, getStatBlock());
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
	public ClientCardTemplate.CardRenderSize getRenderSize(){
		return size;
	}
	public void setRenderSize(ClientCardTemplate.CardRenderSize size){
		this.size=size;
	}

}
