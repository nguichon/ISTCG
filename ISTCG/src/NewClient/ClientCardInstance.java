package NewClient;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import Shared.StatBlock;

import Client.CardTemplate.CardRenderSize;

public class ClientCardInstance extends Canvas {

	String name;
	String[] description;
	ArrayList<String> stats;
	CardRenderSize size;
	ClientMain main;
	ClientCardTemplate template;
	public ClientCardInstance(Composite parent, int style, ClientMain main) {
		super(parent, style);
		this.main=main;
		
	}
	public ArrayList<String> getStats(){
		return stats;
	}
	public String getStat(String stat){
		switch(StatBlock.StatType.valueOf(stat)){
		case ATTACK:
			return (stats.get(StatBlock.StatType.ATTACK.ordinal())!=null?stats.get(StatBlock.StatType.ATTACK.ordinal()):template.get("ATTACK")); 
		case DEFENSE:
			return (stats.get(StatBlock.StatType.DEFENSE.ordinal())!=null?stats.get(StatBlock.StatType.DEFENSE.ordinal()):template.get("DEFENSE"));
		case POWER:
			return (stats.get(StatBlock.StatType.POWER.ordinal())!=null?stats.get(StatBlock.StatType.POWER.ordinal()):template.get("POWER"));
		case STRUCTURE:
			return (stats.get(StatBlock.StatType.STRUCTURE.ordinal())!=null?stats.get(StatBlock.StatType.STRUCTURE.ordinal()):template.get("STRUCTURE"));
		case HARD_POINTS:
			return (stats.get(StatBlock.StatType.HARD_POINTS.ordinal())!=null?stats.get(StatBlock.StatType.HARD_POINTS.ordinal()):template.get("HARD_POINTS"));
		case DELAY:
			return (stats.get(StatBlock.StatType.DELAY.ordinal())!=null?stats.get(StatBlock.StatType.DELAY.ordinal()):template.get("DELAY"));
		case METAL:
			return (stats.get(StatBlock.StatType.METAL.ordinal())!=null?stats.get(StatBlock.StatType.METAL.ordinal()):template.get("METAL"));
		case ENERGY:
			return (stats.get(StatBlock.StatType.ENERGY.ordinal())!=null?stats.get(StatBlock.StatType.ENERGY.ordinal()):template.get("ENERGY"));
		case TECH:
			return (stats.get(StatBlock.StatType.TECH.ordinal())!=null?stats.get(StatBlock.StatType.TECH.ordinal()):template.get("TECH"));
		default: return null;
		}
	}
	
	

}
