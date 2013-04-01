package editor;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

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

import NewClient.ClientCardTemplate;
import NewClient.ClientCardTemplateManager;
import OldClient.ImageManager;
import Shared.CardTypes;
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
	private static final Combo m_CardType = new Combo( m_MainInfo, SWT.NONE );
	private static final Button m_SaveButton = new Button( m_MainInfo, SWT.NONE );
	
	private static final List m_ListOfTargets = new List( m_SubInfo, SWT.V_SCROLL );
	private static final Button[] m_Conditions = new Button[ CardTypes.values().length ];
	private static final Button m_NewTargetButton = new Button( m_SubInfo, SWT.NONE );
	private static final Button m_SaveTargetButton = new Button( m_SubInfo, SWT.NONE );
	
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
		m_NewTargetButton.setText( "New" );
		m_NewTargetButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				NewTarget();
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
				// TODO Auto-generated method stub
				
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
				// TODO Auto-generated method stub
				
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
		
		m_SaveAllButton.setText( "Save All Card Files" );
		m_SaveAllButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SaveAll();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
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
				// TODO Auto-generated method stub
				
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
				m_SaveAllButton.setBounds( 0, ca.height - 23, ca.width, 23 );
			}
		});
		m_SubInfo.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Rectangle ca = m_SubInfo.getClientArea();
				m_ListOfTargets.setBounds( 0,0,ca.width / 3,ca.height-23 );
				m_ListOfTargets.getVerticalBar().setVisible( true );
				m_NewTargetButton.setBounds( 0,ca.height-23,ca.width / 6,23 );
				m_SaveTargetButton.setBounds( ca.width / 6,ca.height-23,ca.width / 6,23 );
				//m_SaveAllButton.setBounds( 0, ca.height - 23, ca.width, 23 );
				for( CardTypes ct : CardTypes.values() ) {
					m_Conditions[ct.ordinal()].setBounds( ca.width / 3 + 1, ct.ordinal() * 23, 100, 23 );
				}
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
				m_Damage.setBounds( 96, 17 * 4 + 2, 48, 24);
				m_DamageIcon.setBounds( 72, 17 * 4 + 2, 24, 24);
				m_Attack.setBounds( 24, 17 * 4 + 2, 48, 24);
				m_AttackIcon.setBounds( 0, 17 * 4 + 2, 24, 24);
				m_Defense.setBounds( 168, 17 * 4 + 2, 48, 24);
				m_DefenseIcon.setBounds( 144, 17 * 4 + 2, 24, 24);
//				m_Structure
//				m_StructureIcon
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
	
	protected static void SaveTarget() {
		if( m_CurrentTargetCondition != null ) {
			for( CardTypes ct : CardTypes.values() ) {
				m_CurrentTargetCondition.m_CardType[ ct.ordinal() ] = m_Conditions[ct.ordinal()].getSelection();
			}
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
			// TODO Auto-generated catch block
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
//		m_Structure
		m_CardType.setText( m_CurrentCardToEdit.getCardType().name() );
		
		m_ListOfTargets.removeAll();
		int i = 0;
		for( TargetingCondition tc : m_CurrentCardToEdit.getTargets() ) {
			m_ListOfTargets.add( String.format("%02d", i++) );
		}
	}
	
	public static void Save() { 
		if( m_CurrentCardToEdit != null ) {
			m_CurrentCardToEdit.setCardName( m_Name.getText() );
			m_CurrentCardToEdit.setCardText( m_Description.getText() );
			m_CurrentCardToEdit.setCardFlavor( m_Flavor.getText() );
			m_CurrentCardToEdit.setCardType( CardTypes.valueOf( m_CardType.getText() ) );

			m_ListOfTargets.removeAll();
			int i = 0;
			for( TargetingCondition tc : m_CurrentCardToEdit.getTargets() ) {
				m_ListOfTargets.add( String.format("%02d", i++) );
			}
		}
	}
	public static void SaveAll() {
		ClientCardTemplate cct = null;
		
		ResultSet rs = Database.get().quickQuery( "SELECT * FROM CARDS;" );
		try {
			while(rs.next()) {
				try {
					cct = ClientCardTemplateManager.get().GetClientCardTemplate( rs.getInt( "id" ) );
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Marshaller writer;
				try {
					writer = JAXBContext.newInstance(
							ClientCardTemplate.class).createMarshaller();
					writer.marshal( cct, new File(DEFAULT_CARD_PATH + cct.getCardID() + ".xml" ) );
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
