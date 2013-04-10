package editor;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import Shared.GameZones;

import NewClient.ClientCardTemplate;
import NewClient.ClientCardTemplateManager;
import OldClient.ImageManager;
import Shared.CardTypes;
import Shared.StatBlock;
import Shared.StatBlock.StatType;
import Shared.TargetingCondition;

import server.Database;
import server.ServerMain;

public class CardEditor {
	private static String DEFAULT_CARD_PATH = System.getProperty("user.dir") + "/data/cards/card_";

	public static final Display display = Display.getDefault();
	public static final Shell shell = new Shell( SWT.NO_REDRAW_RESIZE | SWT.SHELL_TRIM );
	public static Canvas big_info;
	public static int current_card = -1;
	private static int count = 0;

	private static final Composite m_AllCards = new Composite( shell, SWT.BORDER );
	private static final Composite m_CurrentCard = new Composite( shell, SWT.BORDER );
	private static final Composite m_MainInfo = new Composite( m_CurrentCard, SWT.BORDER );
	private static final Composite m_SubInfo = new Composite( m_CurrentCard, SWT.BORDER );

	private static final List m_ListOfCards = new List( m_AllCards, SWT.V_SCROLL );
	private static final Button m_SaveAllButton = new Button( m_AllCards, SWT.NONE );
	private static final Button m_NewCardButton = new Button( m_AllCards, SWT.NONE );

	private static final Text m_Name = new Text( m_MainInfo, SWT.NONE );
	private static final Text m_Description = new Text( m_MainInfo, SWT.MULTI | SWT.WRAP );
	private static final Text m_Flavor = new Text( m_MainInfo, SWT.MULTI | SWT.WRAP );
	private static final Spinner m_Damage = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_DamageIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Spinner m_Structure = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_StructureIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Spinner m_Attack = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_AttackIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Spinner m_Defense = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_DefenseIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Spinner m_Metal = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_MetalIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Spinner m_Energy = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_EnergyIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Spinner m_Tech = new Spinner( m_MainInfo, SWT.NONE );
	private static final Canvas m_TechIcon = new Canvas( m_MainInfo, SWT.NONE );
	private static final Combo m_CardType = new Combo( m_MainInfo, SWT.NONE );
	private static final Button m_SaveButton = new Button( m_MainInfo, SWT.NONE );

	private static final List m_ListOfTargets = new List( m_SubInfo, SWT.V_SCROLL );
	private static final Button[] m_Conditions = new Button[ CardTypes.values().length ];
	private static final Button[] m_ConditionsLocation = new Button[ GameZones.values().length ];
	private static final Button m_ConditionsMine = new Button( m_SubInfo, SWT.TOGGLE );
	private static final Button m_ConditionsOpp = new Button( m_SubInfo, SWT.TOGGLE );
	private static final Button m_NewTargetButton = new Button( m_SubInfo, SWT.NONE );
	private static final Button m_RemoveTargetButton = new Button( m_SubInfo, SWT.NONE );
	private static final Button m_SaveTargetButton = new Button( m_SubInfo, SWT.NONE );
	private static ArrayList<TargetingCondition> m_CurrentListOfConditions;
	private static TargetingCondition m_CurrentCondition;

	private static ClientCardTemplate m_CurrentCardToEdit = null;
	private static TargetingCondition m_CurrentTargetCondition = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database.initialize();
			ClientCardTemplateManager.get().Initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		populateCards();
		for( CardTypes ct : CardTypes.values() ) {
			m_CardType.add( ct.name() );
			m_Conditions[ct.ordinal()] = new Button( m_SubInfo, SWT.TOGGLE );
			m_Conditions[ct.ordinal()].setText( ct.name() );
		}
		for( GameZones gz : GameZones.values() ) {
			m_CardType.add( gz.name() );
			m_ConditionsLocation[gz.ordinal()] = new Button( m_SubInfo, SWT.TOGGLE );
			m_ConditionsLocation[gz.ordinal()].setText( gz.name() );
		}
		m_ConditionsMine.setText( "Player's" );
		m_ConditionsOpp.setText( "Opponent's" );
		m_NewTargetButton.setText( "+" );
		m_NewTargetButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				NewTarget();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// 

			}

		});
		m_ListOfTargets.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if( m_ListOfTargets.getSelectionIndex() >= 0 && m_ListOfTargets.getSelectionIndex() < m_CurrentListOfConditions.size() ) {
					m_CurrentTargetCondition = m_CurrentListOfConditions.get( m_ListOfTargets.getSelectionIndex());
					UpdateTarget();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// 
				
			}
			
		});
		m_RemoveTargetButton.setText( "-" );
		m_RemoveTargetButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RemoveTarget();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});
		m_SaveTargetButton.setText( "Save" );
		m_SaveTargetButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SaveTarget();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// 

			}

		});
		m_SaveButton.setText( "Save Changes" );
		m_SaveButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Save();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// 

			}

		});
		m_Damage.setMinimum( -1 );
		m_DamageIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "icon-damage-inverse.png"), 0, 0 );
			}

		});
		m_Attack.setMinimum( -1 );
		m_AttackIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "icon-attack-inverse.png"), 0, 0 );
			}

		});
		m_Defense.setMinimum( -1 );
		m_DefenseIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "icon-defense-inverse.png"), 0, 0 );
			}

		});
		m_Metal.setMinimum( 0 );
		m_MetalIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "resource_metal_icon.png"), 0, 0 );
			}

		});
		m_Tech.setMinimum( 0 );
		m_TechIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "resource_tech_icon.png"), 0, 0 );
			}

		});
		m_Structure.setMinimum( -1 );
		m_StructureIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "icon-structure-good.png"), 0, 0 );
			}

		});
		m_Energy.setMinimum( 0 );
		m_EnergyIcon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( ImageManager.get().GetImage( "resource_energy_icon.png"), 0, 0 );
			}

		});

		m_SaveAllButton.setText( "Save All Card Files" );
		m_SaveAllButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SaveAll();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// 

			}

		});
		m_NewCardButton.setText( "New Card" );
		m_NewCardButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				NewCard();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// 

			}

		});

		m_ListOfCards.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				m_CurrentCardToEdit = ClientCardTemplateManager.get().GetClientCardTemplate( Integer.valueOf( m_ListOfCards.getItem( m_ListOfCards.getSelectionIndex() ).substring(0,5) ) );
				UpdateField();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		shell.addListener( SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Rectangle ca = shell.getClientArea();
				m_AllCards.setBounds( 0, 0, ca.width / 3, ca.height );
				m_CurrentCard.setBounds( ca.width / 3, 0, 2 * ca.width / 3, ca.height );
			}
		});

		m_AllCards.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Rectangle ca = m_AllCards.getClientArea();
				m_ListOfCards.setBounds( 0,0,ca.width,ca.height-23 );
				m_ListOfCards.getVerticalBar().setVisible( true );
				m_SaveAllButton.setBounds( 0, ca.height - 23, ca.width / 2, 23 );
				m_NewCardButton.setBounds( ca.width / 2, ca.height - 23, ca.width / 2, 23 );
			}
		});
		m_SubInfo.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Rectangle ca = m_SubInfo.getClientArea();
				m_ListOfTargets.setBounds( 0,0,ca.width / 3,ca.height-23 );
				m_ListOfTargets.getVerticalBar().setVisible( true );
				m_SaveTargetButton.setBounds( 0,ca.height-23,ca.width / 6,23 );
				m_NewTargetButton.setBounds( ca.width / 6,ca.height-23,ca.width / 12,23 );
				m_RemoveTargetButton.setBounds( ca.width / 6 + ca.width / 12,ca.height-23,ca.width / 12,23 );
				//m_SaveAllButton.setBounds( 0, ca.height - 23, ca.width, 23 );
				for( CardTypes ct : CardTypes.values() ) {
					m_Conditions[ct.ordinal()].setBounds( ca.width / 3 + 1, ct.ordinal() * 23, 100, 23 );
				}
				for( GameZones gz : GameZones.values() ) {
					m_ConditionsLocation[gz.ordinal()].setBounds( 2 * ca.width / 3 + 2, gz.ordinal() * 23, 100, 23 );
				}
				m_ConditionsMine.setBounds( ca.width / 3 + 1, ca.height - 23, 100, 23 );
				m_ConditionsOpp.setBounds( 2 * ca.width / 3 + 1, ca.height - 23, 100, 23 );
			}
		});
		m_CurrentCard.addListener( SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Rectangle ca = m_CurrentCard.getClientArea();
				m_MainInfo.setBounds( 0,0,ca.width,ca.height / 2 );
				m_SubInfo.setBounds( 0,ca.height / 2,ca.width,ca.height / 2 );
			}
		});
		m_MainInfo.addListener( SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Rectangle ca = m_MainInfo.getClientArea();
				m_SaveButton.setBounds( ca.width - 100, ca.height - 23, 100, 23 );

				m_Name.setBounds( 0, 0, ca.width / 2, 17 );
				m_Description.setBounds( ca.width / 2 + 1, 0, ca.width / 2 - 1, 17 * 4 + 1 );
				m_Flavor.setBounds(  0, 17 + 1, ca.width / 2, 17 * 3 );
				m_Attack.setBounds( 24, 17 * 4 + 2, 48, 24);
				m_AttackIcon.setBounds( 0, 17 * 4 + 2, 24, 24);
				m_Damage.setBounds( 96, 17 * 4 + 2, 48, 24);
				m_DamageIcon.setBounds( 72, 17 * 4 + 2, 24, 24);
				m_Defense.setBounds( 168, 17 * 4 + 2, 48, 24);
				m_DefenseIcon.setBounds( 144, 17 * 4 + 2, 24, 24);
				m_Structure.setBounds( 240, 17 * 4 + 2, 48, 24);
				m_StructureIcon.setBounds( 216, 17 * 4 + 2, 24, 24);
				m_Metal.setBounds( 24, 17 * 4 + 26, 48, 24);
				m_MetalIcon.setBounds( 0, 17 * 4 + 26, 24, 24);
				m_Energy.setBounds( 96, 17 * 4 + 26, 48, 24);
				m_EnergyIcon.setBounds( 72, 17 * 4 + 26, 24, 24);
				m_Tech.setBounds( 168, 17 * 4 + 26, 48, 24);
				m_TechIcon.setBounds( 144, 17 * 4 + 26, 24, 24);
				m_CardType.setBounds( 0, ca.height - 23, ca.width - 100, 23 );
			}
		});


		shell.setSize(585, 500);
		shell.setMinimumSize( 100, 200 );
		shell.setText("Card Editor");
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected static void UpdateTarget() {
		for( CardTypes ct : CardTypes.values() ) {
			m_Conditions[ct.ordinal()].setSelection(m_CurrentTargetCondition.m_CardType[ ct.ordinal() ]);
		}
		for( GameZones ct : GameZones.values() ) {
			m_ConditionsLocation[ct.ordinal()].setSelection(m_CurrentTargetCondition.m_Location[ ct.ordinal() ]);
		}
		m_ConditionsMine.setSelection( m_CurrentTargetCondition.m_CanTargetMine);
		m_ConditionsOpp.setSelection( m_CurrentTargetCondition.m_CanTargetOpponent);
	}

	protected static void RemoveTarget() {
		if( m_CurrentTargetCondition != null ) {
			m_CurrentListOfConditions.remove( m_CurrentTargetCondition );
			m_CurrentTargetCondition = null;
			m_ListOfTargets.removeAll();
			int i = 0;
			for( TargetingCondition tc : m_CurrentListOfConditions ) {
				m_ListOfTargets.add( String.format("%02d", i++) );
			}
		}
	}

	static int m_NewCardCounter = 0;
	protected static void NewCard() {
		ClientCardTemplate ct = ClientCardTemplateManager.get().NewTemplate( -1 * ++m_NewCardCounter );
		m_ListOfCards.add( String.format("%05d - %s", ct.getCardID(), ct.getCardName() ) );
	}

	protected static void SaveTarget() {
		if( m_CurrentTargetCondition != null ) {
			for( CardTypes ct : CardTypes.values() ) {
				m_CurrentTargetCondition.m_CardType[ ct.ordinal() ] = m_Conditions[ct.ordinal()].getSelection();
				System.out.println( ct.name() + " is " + m_Conditions[ct.ordinal()].getSelection() );
			}
			for( GameZones ct : GameZones.values() ) {
				m_CurrentTargetCondition.m_Location[ ct.ordinal() ] = m_ConditionsLocation[ct.ordinal()].getSelection();
				System.out.println( ct.name() + " is " + m_ConditionsLocation[ct.ordinal()].getSelection() );
			}
			m_CurrentTargetCondition.m_CanTargetMine = m_ConditionsMine.getSelection( );
			m_CurrentTargetCondition.m_CanTargetOpponent = m_ConditionsOpp.getSelection( );
		}
	}

	protected static void NewTarget() {
		TargetingCondition t = new TargetingCondition();
		m_CurrentCardToEdit.getTargets().add( t );
		m_ListOfTargets.add( String.format("%02d", m_CurrentCardToEdit.getTargets().size() - 1) );

	}

	public static void populateCards() {
		ResultSet rs = Database.get().quickQuery( "SELECT * FROM CARDS ORDER BY id;" );

		try {
			while(rs.next()) {
				CreateCard(rs);
			}
		} catch (SQLException e) {
			ServerMain.ConsoleMessage('!', "A card failed to load, fatal error");
			e.printStackTrace();
		}
	}

	public static void CreateCard( ResultSet rs ) {
		try {
			ClientCardTemplate ct = ClientCardTemplateManager.get().GetClientCardTemplate( rs.getInt("id") );
			m_ListOfCards.add( String.format("%05d - %s", ct.getCardID(), ct.getCardName() ) );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void UpdateField() {
		m_Name.setText( m_CurrentCardToEdit.getCardName() );
		m_Description.setText( m_CurrentCardToEdit.getCardText() );
		m_Flavor.setText( m_CurrentCardToEdit.getCardFlavor() );
		m_Damage.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.POWER )));
		m_Attack.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.ATTACK )));
		m_Defense.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.DEFENSE )));
		m_Structure.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.STRUCTURE )));
		m_Metal.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.METAL )));
		m_Energy.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.ENERGY )));
		m_Tech.setSelection( Integer.valueOf(m_CurrentCardToEdit.GetStatValue( StatType.TECH )));
		m_CardType.setText( m_CurrentCardToEdit.getCardType().name() );

		m_CurrentListOfConditions = m_CurrentCardToEdit.getTargets();
		m_ListOfTargets.removeAll();
		int i = 0;
		for( TargetingCondition tc : m_CurrentListOfConditions ) {
			m_ListOfTargets.add( String.format("%02d", i++) );
		}
	}

	public static void Save() { 
		if( m_CurrentCardToEdit != null ) {
			m_CurrentCardToEdit.setCardName( m_Name.getText() );
			m_CurrentCardToEdit.setCardText( m_Description.getText() );
			m_CurrentCardToEdit.setCardFlavor( m_Flavor.getText() );
			m_CurrentCardToEdit.setCardType( CardTypes.valueOf( m_CardType.getText() ) );

			ArrayList<StatBlock> stats = new ArrayList<StatBlock>();

			if( m_Damage.getSelection() >= 0 ) {
				stats.add( new StatBlock( StatType.POWER, m_Damage.getSelection()));
			}
			if( m_Attack.getSelection() >= 0 ) {
				stats.add( new StatBlock( StatType.ATTACK, m_Attack.getSelection()));
			}
			if( m_Defense.getSelection() >= 0 ) {
				stats.add( new StatBlock( StatType.DEFENSE, m_Defense.getSelection()));
			}
			if( m_Structure.getSelection() >= 0 ) {
				stats.add( new StatBlock( StatType.STRUCTURE, m_Structure.getSelection()));
			}
			if( m_Metal.getSelection() > 0 ) {
				stats.add( new StatBlock( StatType.METAL, m_Metal.getSelection()));
			}
			if( m_Energy.getSelection() > 0 ) {
				stats.add( new StatBlock( StatType.ENERGY, m_Energy.getSelection()));
			}
			if( m_Tech.getSelection() > 0 ) {
				stats.add( new StatBlock( StatType.TECH, m_Tech.getSelection()));
			}

			m_CurrentCardToEdit.setStats( stats );
			m_CurrentCardToEdit.setTargets( m_CurrentListOfConditions );

			m_ListOfTargets.removeAll();
			
			/*int i = 0;
			for( TargetingCondition tc : m_CurrentCardToEdit.getTargets() ) {
				m_ListOfTargets.add( String.format("%02d", i++) );
			}*/
		}
	}
	public static void SaveAll() {
		ClientCardTemplate cct = null;

		ArrayList< ClientCardTemplate > cardTemplates = ClientCardTemplateManager.get().GetAllCards();

		Marshaller writer = null;
		try {
			writer = JAXBContext.newInstance(
					ClientCardTemplate.class).createMarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		for( ClientCardTemplate ct : cardTemplates ) {
			ResultSet rs = Database.get().quickQuery( "SELECT * FROM CARDS WHERE id = " + ct.getCardID() + ";" );
			try {
				if( rs.next() ) {
					//edit
					String query = String.format( "UPDATE cards SET name = '%s', description = '%s', flavor_text = '%s', attack = %d, power = %d, defense = %d, structure = %d, metal = %d, energy = %d, tech = %d, type = '%s' WHERE id = %d;", 
							ct.getCardName(),
							ct.getCardText(),
							ct.getCardFlavor(),
							ct.GetStatValue( StatType.ATTACK ),
							ct.GetStatValue( StatType.POWER ),
							ct.GetStatValue( StatType.DEFENSE ),
							ct.GetStatValue( StatType.STRUCTURE ),
							ct.GetStatValue( StatType.METAL ),
							ct.GetStatValue( StatType.ENERGY ),
							ct.GetStatValue( StatType.TECH ),
							ct.getCardType().name(),
							ct.getCardID());
					System.out.println( query );
					Database.get().quickInsert( query );
				} else {
					String query = String.format( "INSERT INTO cards( id, created_at, modified_at, name, description, flavor_text, attack, power, defense, structure, hard_points, delay, metal, energy, tech, type)" + 
							"VALUES (DEFAULT, DEFAULT, DEFAULT, '%s', '%s', '%s', %d, %d, %d, %d, -1, -1, %d, %d, %d, '%s') RETURNING id;", 
							ct.getCardName(),
							ct.getCardText(),
							ct.getCardFlavor(),
							ct.GetStatValue( StatType.ATTACK ),
							ct.GetStatValue( StatType.POWER ),
							ct.GetStatValue( StatType.DEFENSE ),
							ct.GetStatValue( StatType.STRUCTURE ),
							ct.GetStatValue( StatType.METAL ),
							ct.GetStatValue( StatType.ENERGY ),
							ct.GetStatValue( StatType.TECH ),
							ct.getCardType().name());
					System.out.println( query );
					rs = Database.get().quickQuery( query );
					if( rs.next() ) {
						ct.setCardID( rs.getInt( "id") );
					}
				}
				writer.marshal( ct, new File(DEFAULT_CARD_PATH + ct.getCardID() + ".xml" ) );
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}
}
