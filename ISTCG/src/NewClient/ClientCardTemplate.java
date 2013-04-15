package NewClient;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

//import org.eclipse.swt.graphics.Image;

import OldClient.ImageManager;
import Shared.CardTypes;
import Shared.StatBlock;
import Shared.TargetingCondition;
import Shared.StatBlock.StatType;

@XmlRootElement(namespace = "card template")
public class ClientCardTemplate {
	public enum CardRenderSize {
		TINY(30, 40), SMALL(90, 120), MEDIUM(180, 240), LARGE(300, 400);

		private int m_SizeX;
		private int m_SizeY;

		private CardRenderSize(int x, int y) {
			m_SizeX = x;
			m_SizeY = y;
		}

		public int getWidth() { return m_SizeX; }

		public int getHeight() { return m_SizeY; }
	}
	private enum CardImageAssets {
		CARD_CORNER_BOTTOM_LEFT( "card_border_corner_bottom_left.png" ), 
		CARD_CORNER_BOTTOM_RIGHT( "card_border_corner_bottom_right.png" ), 
		CARD_CORNER_TOP_LEFT( "card_border_corner_top_left.png" ), 
		CARD_CORNER_TOP_RIGHT( "card_border_corner_top_right.png" ), 
		CARD_EDGE_LEFT( "card_border_edge_left.png" ),
		CARD_EDGE_RIGHT( "card_border_edge_right.png" ),
		CARD_EDGE_TOP( "card_border_edge_top.png" ),
		CARD_EDGE_BOTTOM( "card_border_edge_bottom.png" ),
		TEXT_CORNER_BOTTOM_LEFT( "text_box_corner_bottom_left.png" ), 
		TEXT_CORNER_BOTTOM_RIGHT( "text_box_corner_bottom_right.png" ), 
		TEXT_CORNER_TOP_LEFT( "text_box_corner_top_left.png" ), 
		TEXT_CORNER_TOP_RIGHT( "text_box_corner_top_right.png" ), 
		TEXT_EDGE_LEFT( "text_box_edge_left.png" ),
		TEXT_EDGE_RIGHT( "text_box_edge_right.png" ),
		TEXT_EDGE_TOP( "text_box_edge_top.png" ),
		TEXT_EDGE_BOTTOM( "text_box_edge_bottom.png" ),
		TEXT_BODY("text_box_body.png");

		private CardImageAssets( String path ) { m_ImagePath = path; }
		private String m_ImagePath;
		public String path() { return m_ImagePath; }
	}

	private CardTypes m_CardType = CardTypes.RESOURCE;
	private int m_CardID = -1;
	private ArrayList<StatBlock> m_Stats;
	private String m_CardName = "";
	private String m_CardFlavor = "";
	private String m_CardText = "";
	private boolean m_Fast = false;

	// private static Display display = null;

	private String m_BGImage;
	private ArrayList<TargetingCondition> m_Targets = new ArrayList<TargetingCondition>();

	private static Rectangle frameBounds =  ImageManager.get().GetImage(CardImageAssets.CARD_CORNER_TOP_RIGHT.path()).getBounds();
	/**
	 * This method draws a card using the passed GC. It will use the default
	 * stat blocks stored in this class unless overwritten by passed StatBlock
	 * 
	 * @param targetGC
	 *            GC to use when drawing this card.
	 * @param stats
	 *            StatBlocks to overwrite, can be empty or have previously
	 *            unknown StatBlocks.
	 * @param damageTaken 
	 */
	public void Render(GC targetGC, CardRenderSize size, ArrayList<StatBlock> stats, int damageTaken) {
		Image toRender = ImageManager.get().GetImage( m_BGImage );

		stats = null;

		switch( size ) {
		case LARGE:
			//Draw Image
			targetGC.drawImage( toRender, 
					0, 0, toRender.getBounds().width, toRender.getBounds().height,
					frameBounds.width / 4, frameBounds.height / 4, size.getWidth() - frameBounds.width / 2, size.getHeight() - frameBounds.height / 2 );
			if( !(m_CardText.isEmpty() && m_CardFlavor.isEmpty()) ) { RenderTextBox( targetGC, size ); RenderCardText( targetGC, size );}
			RenderName( targetGC, size );
			RenderBorder( targetGC, size );
			RenderCardFlavor( targetGC, size );
			for( StatBlock sb : m_Stats ) {
				StatBlock toUse = sb;
				if( stats != null ) {
					for( StatBlock sb2 : stats ) {
						if( sb2.m_Type == toUse.m_Type ) { toUse = sb2; }
					}
				}

				switch( toUse.m_Type ) {
				case ATTACK:
					RenderAttack( toUse, targetGC, size );
					break;
				case DEFENSE:
					RenderDefense( toUse, targetGC, size );
					break;
				case POWER:
					RenderPower( toUse, targetGC, size );
					break;
				case STRUCTURE:
					RenderStructure( toUse, targetGC, size );
					break;
				case METAL:
					RenderMetal( toUse, targetGC, size );
					break;
				case ENERGY:
					RenderEnergy( toUse, targetGC, size );
					break;
				case TECH:
					RenderTech( toUse, targetGC, size );
					break;
				default:
					break;
				}
			}
			if( damageTaken > 0 ) {
				RenderDamage( targetGC, damageTaken, size );
			}
			break;
		default:
			targetGC.drawImage( toRender, 0, 0, toRender.getBounds().width, toRender.getBounds().height, 0, 0, size.getWidth(), size.getHeight() );
			targetGC.drawRectangle( 0, 0, size.getWidth() - 1, size.getHeight() - 1 );
			targetGC.drawRectangle( 1, 1, size.getWidth() - 3, size.getHeight() - 3 );
			//RenderBorder( targetGC, size );
			break;
		}
	}

	private void RenderDamage( GC targetGC, int damageTaken, CardRenderSize size ) {
		if( size == CardRenderSize.LARGE && damageTaken != 0 ) {
			Image icon =  ImageManager.get().GetImage("icon-structure-bad.png");
			for( int i = 0; i < damageTaken; i++ ) {
				if( i % 2 == 0 ) {
					targetGC.drawImage( icon,
							0, 0, icon.getBounds().width, icon.getBounds().height,
							0 + frameBounds.width - 8, size.getHeight() - ((frameBounds.height + 2) * ((i/2) + 2)) + 8, 24, 24 );
				} else {
					targetGC.drawImage( icon,
							0, 0, icon.getBounds().width, icon.getBounds().height,
							0 + frameBounds.width - 8 + 26, size.getHeight() - ((frameBounds.height + 2) * ((i/2) + 2)) + 8 - 12, 24, 24 );

				}
			}
		}
	}

	public int GetStatValue( StatType type ) {
		if( m_Stats != null ) {
			for( StatBlock s : m_Stats ) {
				if( s.m_Type == type )
					return s.m_Value;
			}
		}
		return -1;
	}

	private static final Font NAME_FONT = new Font( Display.getDefault(), "Monospaced", 16, SWT.BOLD);
	private static final Font TEXT_FONT = new Font( Display.getDefault(), "Monospaced", 12, SWT.NONE);
	private static final Font FLAVOR_FONT = new Font( Display.getDefault(), "Monospaced", 9, SWT.ITALIC);
	private void RenderName( GC targetGC, CardRenderSize size ) {
		if( size == CardRenderSize.LARGE ) {
			Image textBox = ImageManager.get().GetImage( "card-element-text-box.png" );
			targetGC.setFont(NAME_FONT);
			int length = size.getWidth() - 75;//targetGC.getFontMetrics().getAverageCharWidth() * m_CardName.length();
			targetGC.drawImage( textBox,
					0, 0, textBox.getBounds().width, textBox.getBounds().height,
					size.getWidth() - length, 0, length, 36);
			targetGC.drawText( m_CardName, size.getWidth() - length + 5, 12, true );
		}
	}
	private static void RenderTextBox( GC targetGC, CardRenderSize size ) {
		Image textBox = ImageManager.get().GetImage( "card-element-text-box.png" );
		targetGC.drawImage( textBox,
				0, 0, textBox.getBounds().width, textBox.getBounds().height,
				75, size.getHeight() - size.getHeight() / 3 - size.getHeight() / 9, size.getWidth() - 75, size.getHeight() / 3);



	}
	private void RenderCardText( GC targetGC, CardRenderSize size ) {
		targetGC.setFont( TEXT_FONT );
		int width = targetGC.getFontMetrics().getAverageCharWidth();
		int height = targetGC.getFontMetrics().getHeight();
		int space = targetGC.getCharWidth( ' ' );
		int lineWidth = size.getWidth() - 75 - 8 - 12;
		String[] words = m_CardText.split(" ");
		int running_length = 0;
		int lines = 0;
		String s = "";
		for( int i = 0; i < words.length; i++ ) {
			if( running_length + (words[i].length() * width) >= lineWidth ) {
				targetGC.drawText( 	s,  
						75 + 8, 
						(size.getHeight() - size.getHeight() / 3 - size.getHeight() / 9) + (lines * (height + 2)) + 5, true);
				running_length = 0;
				s = "";
				lines++;
			}

			running_length += (words[i].length()) * width + space;
			s += words[i] + " ";
		}
		targetGC.drawText( 	s,  
				75 + 8, 
				(size.getHeight() - size.getHeight() / 3 - size.getHeight() / 9) + (lines * (height + 2)) + 5, true);
	}
	private void RenderCardFlavor( GC targetGC, CardRenderSize size ) {
		targetGC.setFont( FLAVOR_FONT );
		int width = targetGC.getFontMetrics().getAverageCharWidth();
		int height = targetGC.getFontMetrics().getHeight();
		int space = targetGC.getCharWidth( ' ' );
		int lineWidth = size.getWidth() - 75 - 25;
		String[] words = m_CardFlavor.split(" ");
		int running_length = 0;
		int lines = 0;
		String s = "";

		for( int i = 0; i < words.length; i++ ) {
			if( running_length + (words[i].length() * width) >= lineWidth ) {
				running_length = 0;
				lines++;
			}

			running_length += (words[i].length()) * width + space;
		}
		lines+=2;
		for( int i = 0; i < words.length; i++ ) {
			if( running_length + (words[i].length() * width) >= lineWidth ) {
				targetGC.drawText( 	s,  
						75 + 8, 
						(size.getHeight() - size.getHeight() / 9 ) - (lines * (height + 2)) + 5, true);
				running_length = 0;
				s = "";
				lines--;
			}

			running_length += (words[i].length()) * width + space;
			s += words[i] + " ";
		}
		targetGC.drawText( 	s,  
				75 + 8, 
				(size.getHeight() - size.getHeight() / 9) - (lines * (height + 2)) + 5, true);
	}
	private static void RenderBorder( GC targetGC, CardRenderSize size ) {
		Rectangle frameBounds =  ImageManager.get().GetImage(CardImageAssets.CARD_CORNER_TOP_RIGHT.path()).getBounds();

		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_CORNER_TOP_LEFT.path()), 0, 0);
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_CORNER_TOP_RIGHT.path()), size.getWidth() - frameBounds.width, 0);
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_CORNER_BOTTOM_LEFT.path()), 0, size.getHeight() - frameBounds.height);
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_CORNER_BOTTOM_RIGHT.path()), size.getWidth() - frameBounds.width, size.getHeight() - frameBounds.height);
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_EDGE_LEFT.path()), 
				0, 0, frameBounds.width, frameBounds.height, 
				0, frameBounds.height, frameBounds.width, size.getHeight() - 2 * frameBounds.height );
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_EDGE_RIGHT.path()), 
				0, 0, frameBounds.width, frameBounds.height, 
				size.getWidth() - frameBounds.width, frameBounds.height, frameBounds.width, size.getHeight() - 2 * frameBounds.height );
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_EDGE_TOP.path()), 
				0, 0, frameBounds.width, frameBounds.height, 
				frameBounds.width, 0, size.getWidth() - 2*frameBounds.width, frameBounds.height );
		targetGC.drawImage( ImageManager.get().GetImage(CardImageAssets.CARD_EDGE_BOTTOM.path()), 
				0, 0, frameBounds.width, frameBounds.height, 
				frameBounds.width, size.getHeight() - frameBounds.height, size.getWidth() - 2*frameBounds.width, frameBounds.height );
	}
	//TODO Relativevise this
	private static void RenderMetal( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.METAL && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("resource_metal_icon.png");
			Image box = ImageManager.get().GetImage("card-element-stat-bubble.png");
			int y =  (size.getHeight() - size.getHeight() / 3 - size.getHeight() / 9) - 26;
			targetGC.drawImage( icon,
					0, 0, icon.getBounds().width, icon.getBounds().height,
					75, y, 24, 24 );
			targetGC.drawImage( box,
					0, 0, box.getBounds().width, box.getBounds().height,
					75 + 26, y, 32, 24 );
			targetGC.setFont( NAME_FONT );
			targetGC.drawText( String.valueOf( toRender.m_Value), 75+26+5, y, true);
		}
	}
	private static void RenderEnergy( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.ENERGY && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("resource_energy_icon.png");
			Image box = ImageManager.get().GetImage("card-element-stat-bubble.png");
			int y =  (size.getHeight() - size.getHeight() / 3 - size.getHeight() / 9) - 26;
			targetGC.drawImage( icon,
					0, 0, icon.getBounds().width, icon.getBounds().height,
					75 + 60,y, 24, 24 );
			targetGC.drawImage( box,
					0, 0, box.getBounds().width, box.getBounds().height,
					75 + 60 + 26, y, 32, 24 );
			targetGC.setFont( NAME_FONT );
			targetGC.drawText( String.valueOf( toRender.m_Value),  75 + 60 + 26 + 5, y, true);
		}
	}
	private static void RenderTech( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.TECH && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("resource_tech_icon.png");
			Image box = ImageManager.get().GetImage("card-element-stat-bubble.png");
			int y =  (size.getHeight() - size.getHeight() / 3 - size.getHeight() / 9) - 26;
			targetGC.drawImage( icon,
					0, 0, icon.getBounds().width, icon.getBounds().height,
					75 + 60 + 60, y, 24, 24 );
			targetGC.drawImage( box,
					0, 0, box.getBounds().width, box.getBounds().height,
					75 + 60 + 60 + 26, y, 32, 24 );
			targetGC.setFont( NAME_FONT );
			targetGC.drawText( String.valueOf( toRender.m_Value), 75 + 60 + 60 + 26 + 5, y, true);
		}
	}
	private static void RenderAttack( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.ATTACK && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("icon-attack-inverse.png");
			Image box = ImageManager.get().GetImage("card-element-stat-bubble.png");
			int y = size.getHeight() - 40;
			targetGC.drawImage( icon,
					0, 0, icon.getBounds().width, icon.getBounds().height,
					75, y, 24, 24 );
			targetGC.drawImage( box,
					0, 0, box.getBounds().width, box.getBounds().height,
					75 + 26, y, 32, 24 );
			targetGC.setFont( NAME_FONT );
			targetGC.drawText( String.valueOf( toRender.m_Value), 75 + 26 + 5, y, true);
		}
	}
	private static void RenderPower( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.POWER && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("icon-damage-inverse.png");
			Image box = ImageManager.get().GetImage("card-element-stat-bubble.png");
			int y = size.getHeight() - 40;
			targetGC.drawImage( icon,
					0, 0, icon.getBounds().width, icon.getBounds().height,
					75 + 60, y, 24, 24 );
			targetGC.drawImage( box,
					0, 0, box.getBounds().width, box.getBounds().height,
					75 + 60 + 26, y, 32, 24 );
			targetGC.setFont( NAME_FONT );
			targetGC.drawText( String.valueOf( toRender.m_Value), 75 + 60 + 26 + 5,y, true);
		}
	}
	private static void RenderDefense( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.DEFENSE && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("icon-defense-inverse.png");
			Image box = ImageManager.get().GetImage("card-element-stat-bubble.png");
			int y = size.getHeight() - 40;
			targetGC.drawImage( icon,
					0, 0, icon.getBounds().width, icon.getBounds().height,
					75 + 60 + 60, y, 24, 24 );
			targetGC.drawImage( box,
					0, 0, box.getBounds().width, box.getBounds().height,
					75 + 60 + 60 + 26, y, 32, 24 );
			targetGC.setFont( NAME_FONT );
			targetGC.drawText( String.valueOf( toRender.m_Value), 75 + 60 + 60 + 26 + 5, y, true);
		}
	}
	private static void RenderStructure( StatBlock toRender, GC targetGC, CardRenderSize size ) {
		if( toRender.m_Type == StatBlock.StatType.STRUCTURE && size == CardRenderSize.LARGE && toRender.m_Value != -1 ) {
			Image icon =  ImageManager.get().GetImage("icon-structure-good.png");
			for( int i = 0; i < toRender.m_Value; i++ ) {
				if( i % 2 == 0 ) {
					targetGC.drawImage( icon,
							0, 0, icon.getBounds().width, icon.getBounds().height,
							0 + frameBounds.width - 8, size.getHeight() - ((frameBounds.height + 2) * ((i/2) + 2)) + 8, 24, 24 );
				} else {
					targetGC.drawImage( icon,
							0, 0, icon.getBounds().width, icon.getBounds().height,
							0 + frameBounds.width - 8 + 26, size.getHeight() - ((frameBounds.height + 2) * ((i/2) + 2)) + 8 - 12, 24, 24 );

				}
			}
		}
	}

	public static void RenderBlack(GC targetGC, CardRenderSize size, ArrayList<StatBlock> stats) {
		final Color black = new Color(targetGC.getDevice(), SWT.COLOR_BLACK, 0, 0);
		targetGC.setForeground( black );
		targetGC.setBackground( black );
		targetGC.fillRectangle( 0, 0, size.getWidth(), size.getHeight() );
	}
	private void RenderStats(GC gc, ArrayList<StatBlock> stats){
		for(int i = 0 ; i < stats.size() ; i++){
			int x = 250;
			int y = 50;
			int val = stats.get(i).m_Value;
			String st = ""+val;
			gc.drawText(st, x, y + (i * 25), true);
		}
	}
	private void DrawInfoText(String txt, GC targetGC){
		ArrayList<String> info = new ArrayList<String>(0);
		int i=-1;
		while(txt.length() > 50){
			i=-1;
			for (int bk = 0; bk < 50 ; bk ++){
				if (txt.charAt(bk)==' '){
					i = bk;
				}
			}
			info.add(txt.substring(0, i));
			txt = txt.substring(i+1);
		}
		info.add(txt);

		int x = 25;
		int y = 265;
		for (int j = 0 ; j < info.size() ; j++){
			targetGC.drawText(info.get(j), x, y + (j * 15), true);
		}
	}

	/**
	 * Generates a full text version of this card.
	 * 
	 * @param overwriteStats
	 *            StatBlocks to overwrite, can be empty or have previously
	 *            unknown StatBlocks
	 * @return Returns a String containing all the card information,
	 *         Name/Type/Cost/Abilities/Flavor etc.
	 */
	public String getCardTextCopy(StatBlock... overwriteStats) {
		return 
		m_CardName + "\n" + 
		m_CardType.toString() + "\n" + 
		m_CardText;
	}

	public String getStat( String statType ) {
		return getStat( StatBlock.StatType.valueOf( statType ) );
	}
	public String getStat( StatBlock.StatType statType ) {
		for( StatBlock sb : m_Stats ) {
			if ( sb.m_Type == statType ) { return String.valueOf( sb.m_Value ); }
		}

		return null;
	}

	// ==============================
	// XML FUNCTIONS: Don't fuck them up.
	// ==============================
	@XmlElement(name = "cardID")
	public int getCardID() {
		return m_CardID;
	}

	public void setCardName(String name) {
		m_CardName = name;
	}

	public void setCardID(int id) {
		m_CardID = id;
	}

	@XmlElement(name = "cardType")
	public CardTypes getCardType() {
		return m_CardType;
	}

	public void setCardType(CardTypes type) {
		m_CardType = type;
	}

	@XmlElement(name = "cardArt")
	public String getBGImage() {
		return m_BGImage;
	}

	public void setBGImage(String path) {
		m_BGImage = path;
	}

	@XmlElement(name = "cardText")
	public String getCardText() {
		return m_CardText;
	}

	public void setCardText(String text) {
		m_CardText = text;
	}

	@XmlElement(name = "cardName")
	public String getCardName() {
		return m_CardName;
	}

	public void setCardType(String name) {
		m_CardName = name;
	}

	@XmlElement(name = "cardFlavor")
	public String getCardFlavor() {
		return m_CardFlavor;
	}

	public void setCardFlavor(String flavorText) {
		m_CardFlavor = flavorText;
	}
	
	@XmlElement(name = "fast")
	public boolean getIsCardFast() {
		return m_Fast;
	}

	public void setIsCardFast( boolean b ) {
		m_Fast = b;
	}

	@XmlElementWrapper(name = "statList")
	@XmlElement(name = "stat")
	public void setStats(ArrayList<StatBlock> stats) {
		this.m_Stats = stats;
	}

	public ArrayList<StatBlock> getStats() {
		return m_Stats;
	}

	@XmlElementWrapper(name = "targetList")
	@XmlElement(name = "target")
	public void setTargets(ArrayList<TargetingCondition> trgts) {
		this.m_Targets = trgts;
	}

	public ArrayList<TargetingCondition> getTargets() {
		return m_Targets;
	}
}
