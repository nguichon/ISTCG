package NewClient;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;

import server.games.GamePlayer;
import NewClient.ClientCardInstance.GameZone;
import NewClient.ClientCardTemplate.CardRenderSize;
import Shared.GameResources;
import Shared.GameZones;

public class Game extends Composite {

	ArrayList<StackObject> stacks;
	ClientMain main;
	String ID="";
	String eID="";
	TabItem tab;
	Label lblUsername;
	Label lblEnemy;
	Label lblEhandsize;
	Button btnPass;
	Button btnEnd;
	Label lblHandsize;
	Label lblDecksize;
	Label lblGravesize;
	Label lblEdecksize;
	Label lblEgravesize;
	Label lblEmetal;
	Label lblEenergy;
	Label lblEtech;
	Label lblMetal;
	Label lblEnergy;
	Label lblTech;
	//start at 100 x 465
	Point handPos;
	Point fieldPos;
	Point stackPos;
	Point viewPos;
	ArrayList<ClientCardInstance> cards;
	ArrayList<String> hand;
	ArrayList<String> field;
	ArrayList<String> stack;
	Group grpStack;
	Group grpHand;
	Group group;
	Canvas viewcanvas;
	GC vcgc;
	String vcard;
	boolean active;
	boolean waiting;
	Group group_1;
	Group grpEnemy;
	boolean targeting = false;
	Label lblActionhelp;
	String curattacker = "";
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Game(Composite parent, int style, final ClientMain main, TabItem tab) {
		super(parent, style);
		this.main=main;
		this.tab=tab;
		handPos = new Point(100,465);
		fieldPos = new Point(100, 365);
		stackPos = new Point(10,33);
		viewPos= new Point(630,200);
		setLayout(new FormLayout());
		lblUsername = new Label(this, SWT.NONE);
		FormData fd_lblUsername = new FormData();
		fd_lblUsername.right = new FormAttachment(0, 69);
		fd_lblUsername.top = new FormAttachment(0, 451);
		fd_lblUsername.left = new FormAttachment(0, 10);
		lblUsername.setLayoutData(fd_lblUsername);
		lblUsername.setText("USERNAME");
		
		lblEnemy = new Label(this, SWT.NONE);
		FormData fd_lblEnemy = new FormData();
		fd_lblEnemy.right = new FormAttachment(0, 586);
		fd_lblEnemy.top = new FormAttachment(0, 17);
		fd_lblEnemy.left = new FormAttachment(0, 527);
		lblEnemy.setLayoutData(fd_lblEnemy);
		lblEnemy.setText("ENEMY");
		
		lblEhandsize = new Label(this, SWT.NONE);
		FormData fd_lblEhandsize = new FormData();
		fd_lblEhandsize.right = new FormAttachment(0, 586);
		fd_lblEhandsize.top = new FormAttachment(0, 37);
		fd_lblEhandsize.left = new FormAttachment(0, 527);
		lblEhandsize.setLayoutData(fd_lblEhandsize);
		lblEhandsize.setText("EHANDSIZE");
		
		btnPass = new Button(this, SWT.NONE);
		FormData fd_btnPass = new FormData();
		fd_btnPass.right = new FormAttachment(0, 88);
		fd_btnPass.top = new FormAttachment(0, 10);
		fd_btnPass.left = new FormAttachment(0, 10);
		btnPass.setLayoutData(fd_btnPass);
		btnPass.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				main.sendData("PASS;"+getID());
				disablePass();
			}
		});
		btnPass.setText("PASS");
		
		btnEnd = new Button(this, SWT.NONE);
		FormData fd_btnEnd = new FormData();
		fd_btnEnd.right = new FormAttachment(0, 88);
		fd_btnEnd.top = new FormAttachment(0, 43);
		fd_btnEnd.left = new FormAttachment(0, 10);
		btnEnd.setLayoutData(fd_btnEnd);
		btnEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				main.sendData("END;"+getID());
			}
		});
		btnEnd.setText("END");
		
		lblHandsize = new Label(this, SWT.NONE);
		FormData fd_lblHandsize = new FormData();
		fd_lblHandsize.right = new FormAttachment(0, 69);
		fd_lblHandsize.top = new FormAttachment(0, 465);
		fd_lblHandsize.left = new FormAttachment(0, 10);
		lblHandsize.setLayoutData(fd_lblHandsize);
		lblHandsize.setText("HANDSIZE");
		
		lblDecksize = new Label(this, SWT.NONE);
		FormData fd_lblDecksize = new FormData();
		fd_lblDecksize.right = new FormAttachment(0, 69);
		fd_lblDecksize.top = new FormAttachment(0, 486);
		fd_lblDecksize.left = new FormAttachment(0, 10);
		lblDecksize.setLayoutData(fd_lblDecksize);
		lblDecksize.setText("DECKSIZE");
		
		lblGravesize = new Label(this, SWT.NONE);
		FormData fd_lblGravesize = new FormData();
		fd_lblGravesize.right = new FormAttachment(0, 69);
		fd_lblGravesize.top = new FormAttachment(0, 506);
		fd_lblGravesize.left = new FormAttachment(0, 10);
		lblGravesize.setLayoutData(fd_lblGravesize);
		lblGravesize.setText("GRAVESIZE");
		
		lblEdecksize = new Label(this, SWT.NONE);
		FormData fd_lblEdecksize = new FormData();
		fd_lblEdecksize.right = new FormAttachment(0, 586);
		fd_lblEdecksize.top = new FormAttachment(0, 57);
		fd_lblEdecksize.left = new FormAttachment(0, 527);
		lblEdecksize.setLayoutData(fd_lblEdecksize);
		lblEdecksize.setText("EDECKSIZE");
		
		lblEgravesize = new Label(this, SWT.NONE);
		FormData fd_lblEgravesize = new FormData();
		fd_lblEgravesize.right = new FormAttachment(0, 651);
		fd_lblEgravesize.top = new FormAttachment(0, 37);
		fd_lblEgravesize.left = new FormAttachment(0, 592);
		lblEgravesize.setLayoutData(fd_lblEgravesize);
		lblEgravesize.setText("EGRAVESIZE");
		
		lblEmetal = new Label(this, SWT.NONE);
		FormData fd_lblEmetal = new FormData();
		fd_lblEmetal.right = new FormAttachment(0, 716);
		fd_lblEmetal.top = new FormAttachment(0, 37);
		fd_lblEmetal.left = new FormAttachment(0, 657);
		lblEmetal.setLayoutData(fd_lblEmetal);
		lblEmetal.setText("EMETAL");
		
		lblEenergy = new Label(this, SWT.NONE);
		FormData fd_lblEenergy = new FormData();
		fd_lblEenergy.right = new FormAttachment(0, 716);
		fd_lblEenergy.top = new FormAttachment(0, 57);
		fd_lblEenergy.left = new FormAttachment(0, 657);
		lblEenergy.setLayoutData(fd_lblEenergy);
		lblEenergy.setText("EENERGY");
		
		lblEtech = new Label(this, SWT.NONE);
		FormData fd_lblEtech = new FormData();
		fd_lblEtech.right = new FormAttachment(0, 716);
		fd_lblEtech.top = new FormAttachment(0, 77);
		fd_lblEtech.left = new FormAttachment(0, 657);
		lblEtech.setLayoutData(fd_lblEtech);
		lblEtech.setText("ETECH");
		
		lblMetal = new Label(this, SWT.NONE);
		FormData fd_lblMetal = new FormData();
		fd_lblMetal.right = new FormAttachment(0, 69);
		fd_lblMetal.top = new FormAttachment(0, 526);
		fd_lblMetal.left = new FormAttachment(0, 10);
		lblMetal.setLayoutData(fd_lblMetal);
		lblMetal.setText("METAL");
		
		lblEnergy = new Label(this, SWT.NONE);
		FormData fd_lblEnergy = new FormData();
		fd_lblEnergy.right = new FormAttachment(0, 69);
		fd_lblEnergy.top = new FormAttachment(0, 546);
		fd_lblEnergy.left = new FormAttachment(0, 10);
		lblEnergy.setLayoutData(fd_lblEnergy);
		lblEnergy.setText("ENERGY");
		
		lblTech = new Label(this, SWT.NONE);
		FormData fd_lblTech = new FormData();
		fd_lblTech.right = new FormAttachment(0, 69);
		fd_lblTech.top = new FormAttachment(0, 566);
		fd_lblTech.left = new FormAttachment(0, 10);
		lblTech.setLayoutData(fd_lblTech);
		lblTech.setText("TECH");
		
		grpStack = new Group(this, SWT.NONE);
		FormData fd_grpStack = new FormData();
		fd_grpStack.bottom = new FormAttachment(0, 445);
		fd_grpStack.top = new FormAttachment(0, 74);
		fd_grpStack.left = new FormAttachment(0, 10);
		grpStack.setLayoutData(fd_grpStack);
		grpStack.setText("STACK");
		
		grpHand = new Group(this, SWT.NONE);
		FormData fd_grpHand = new FormData();
		fd_grpHand.bottom = new FormAttachment(0, 690);
		fd_grpHand.right = new FormAttachment(0, 668);
		fd_grpHand.top = new FormAttachment(0, 439);
		fd_grpHand.left = new FormAttachment(0, 94);
		grpHand.setLayoutData(fd_grpHand);
		grpHand.setText("HAND");
		grpHand.setLayout(new GridLayout(7, false));
		
//		group = new Group(this,SWT.NONE);
//		FormData fd_group = new FormData();
//		fd_group.bottom = new FormAttachment(0, 539);
//		fd_group.right = new FormAttachment(0, 1035);
//		fd_group.top = new FormAttachment(0, 94);
//		fd_group.left = new FormAttachment(0, 717);
//		//group.setLayoutData(fd_group);
//		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		vcard = "";
		
		group_1 = new Group(this, SWT.NONE);
		FormData fd_group_1 = new FormData();
		fd_group_1.bottom = new FormAttachment(grpHand, -4);
		fd_group_1.left = new FormAttachment(grpStack, 6);
		fd_group_1.right = new FormAttachment(0, 521);
		group_1.setLayoutData(fd_group_1);
		group_1.setLayout(new GridLayout(1, false));
		
		grpEnemy = new Group(this, SWT.NONE);
		fd_group_1.top = new FormAttachment(grpEnemy, 6);
		FormData fd_grpEnemy = new FormData();
		fd_grpEnemy.bottom = new FormAttachment(0, 233);
		fd_grpEnemy.right = new FormAttachment(0, 521);
		fd_grpEnemy.top = new FormAttachment(0, 10);
		fd_grpEnemy.left = new FormAttachment(0, 94);
		grpEnemy.setLayoutData(fd_grpEnemy);
		grpEnemy.setText("ENEMY");
		grpEnemy.setLayout(new GridLayout(1, false));
		
		lblActionhelp = new Label(this, SWT.NONE);
		FormData fd_lblActionhelp = new FormData();
		fd_lblActionhelp.top = new FormAttachment(0, 74);
		fd_lblActionhelp.left = new FormAttachment(0, 527);
		lblActionhelp.setLayoutData(fd_lblActionhelp);
		lblActionhelp.setText("ACTIONHELP");
		
		
		
		Group grpViewer = new Group(this, SWT.NONE);
		grpViewer.setText("VIEWER");
		grpViewer.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_grpViewer = new FormData();
		fd_grpViewer.top = new FormAttachment(lblEtech, 80);
		fd_grpViewer.right = new FormAttachment(grpHand, 306, SWT.RIGHT);
		fd_grpViewer.bottom = new FormAttachment(lblMetal, 31, SWT.BOTTOM);
		fd_grpViewer.left = new FormAttachment(grpHand, 6);
		grpViewer.setLayoutData(fd_grpViewer);
		viewcanvas = new Canvas(grpViewer, SWT.NONE);
		//viewcanvas.setLayoutData(new FormData());
		//viewcanvas.setParent(group);
		//viewcanvas.setLayoutData(new FormData());
		//viewcanvas.setBounds(new Rectangle(viewcanvas.getParent().getBounds().width-CardRenderSize.LARGE.getWidth(),viewcanvas.getParent().getBounds().height-CardRenderSize.LARGE.getHeight(),CardRenderSize.LARGE.getWidth(),CardRenderSize.LARGE.getHeight()));
		vcgc = new GC(viewcanvas);
		viewcanvas.setLayout(new FillLayout(SWT.HORIZONTAL));
		viewcanvas.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				//play the card in the viewer
				playCard(vcard);
				
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});

		/*
		 * Finally start game stuff
		 */
		//this.loadDeck();
		this.disablePass();
		cards = new ArrayList<ClientCardInstance>();
		stacks = new ArrayList<StackObject>();
		hand = new ArrayList<String>();
		field = new ArrayList<String>();
	}
	
	public void playCard(String cardID){
		System.out.println( hasCardLoaded(cardID) );
		System.out.println( hand.contains(cardID) );
		System.out.println( getActive() );
		System.out.println( findCardById(cardID).getController().equals(main.getPID()) );
		if(!targeting){
		if(hasCardLoaded(cardID)&&hand.contains(cardID)&&getActive()&&findCardById(cardID).getController().equals(main.getPID())){ //HOLY IF STATEMENT BATMAN
			main.sendData("PLAY;"+getID()+";"+cardID);
		}
		if(hasCardLoaded(cardID)&&field.contains(cardID)&&getActive()&&findCardById(cardID).getController().equals(main.getPID())){ //HOLY IF STATEMENT BATMAN
			this.targeting=true; // we are now ready to attack
			lblActionhelp.setText("Select target.");
			System.out.println("Preparing to attack");
			this.curattacker=cardID;
		}
		} else {
			if(hasCardLoaded(cardID)&&field.contains(findCardById(cardID))&&getActive()&&!findCardById(cardID).getController().equals(main.getPID())){ //HOLY IF STATEMENT BATMAN
				this.targeting=false; // we are now ready to attack
				main.sendData("ATTACK;"+this.ID+";"+curattacker+";"+cardID);
				System.out.println(curattacker+" is attaking "+cardID);
				curattacker="";
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public void setID(String s){
		ID=s;
	}
	public String getID(){
		return ID;
	}
	public void setEnemy(String name){
		lblEnemy.setText(name);
	}
	public void setEnemyHandSize(String num){
		lblEhandsize.setText(num);
	}
	public void setEnemyDeckSize(String num){
		lblEdecksize.setText(num);
	}
	public void setEnemyGraveSize(String num){
		lblEgravesize.setText(num);
	}
	public void setPlayer(String name){
		lblUsername.setText(name);
	}
	public void setHandSize(String num){
		lblHandsize.setText(num);
	}
	public void setDeckSize(String num){
		lblDecksize.setText(num);
	}
	public void setGraveSize(String num){
		lblGravesize.setText(num);
	}
	public void setEnemyID(String ID){
		eID=ID;
	}
	public void updateZone(String player, String zone, String val){
		if(player.equals(main.getPID())){
			//This guy
			switch(GameZones.valueOf(zone)){
			case HAND:
				setHandSize(val); break;
			case DECK:
				setDeckSize(val); break;
			case GRAVEYARD:
				setGraveSize(val); break;
			default:
				break;
			}
		} else if(player.equals(eID)){
			switch(GameZones.valueOf(zone)){
			case HAND:
				setEnemyHandSize(val); break;
			case DECK:
				setEnemyDeckSize(val); break;
			case GRAVEYARD:
				setEnemyGraveSize(val); break;
			default:
				break;
			}
		}
	}
	public void setTurn(String player){
		if(player.equals(main.getPID())){
			btnEnd.setEnabled(true);
		} else if(player.equals(eID)){
			btnEnd.setEnabled(false);
		}
	}
	public void enablePass(){
		btnPass.setEnabled(true);
	}
	public void disablePass() {
		btnPass.setEnabled(false);
	}
	//3,100
	public void loadDeck(){
		//default for now
		main.sendData("DECKLIST;"+this.getID()+";3;0,15|1,15|2,10|5,10|15,10");
	}

	public void addToStack(String cardID){
		if(!hasCardLoaded(cardID))
			createCard(cardID);
		ClientCardInstance card = findCardById(cardID);
		card.setZone(GameZone.STACK);
		card.setParent(this.grpStack);
		card.setBounds(grpStack.getBounds().x,grpStack.getBounds().y,card.size.getWidth(),card.size.getWidth());
		card.redraw();
		grpStack.layout();
		card.redraw();
	}
	
	public void addStack(String oID, String[] args){
		StackObject s = new StackObject(grpStack,0,main,oID,this);
		
		//if attack
		if(args[0].equals("ATTACK")){
			s.setParentCard(args[1]);
			s.setTargetCard(args[2]);
		} else if(args[0].equals("ABILITY")){
			s.setParentCard(args[1]);
			s.setAbilityIndex(args[2]);
			if(args.length>3)
				s.setTargetCard(args[3]);
		} else if(args[0].equals("EVENT")){
			s.setText(args[1]);
		}
		s.setTemplate(String.valueOf(findCardById(args[1]).getTID()));
		s.redraw();
		
	}
	
	public StackObject findStackById(String oID){
		for(StackObject s : stacks){
			if(s.getID().equals(oID))
				return s;
		}
		return null;
	}
	
	public void removeStack(String oID){
		if(findStackById(oID)!=null){
			findStackById(oID).dispose();
		}
	}
	
	
	public void removeFromStack(String cardID){
		if(hasCardLoaded(cardID)){
			findCardById(cardID).dispose();
			stack.remove(cardID);
		}
	}
	
	public void addToHand(String cardID){
		if(!hasCardLoaded(cardID))
			createCard(cardID,GameZone.HAND);
//		findCardById(cardID).setBounds(handPos.x, handPos.y, ClientCardTemplate.CardRenderSize.SMALL.getWidth(), ClientCardTemplate.CardRenderSize.SMALL.getHeight());
//		findCardById(cardID).setZone(GameZone.HAND);
//		hand.add(cardID);
//		manageHand();
//		//handPos.x+=64;
		hand.add(cardID);
		ClientCardInstance card = findCardById(cardID);
		card.setZone(GameZone.HAND);
		card.setParent(grpHand);
		card.setBounds(grpHand.getBounds().x,grpHand.getBounds().y,card.size.getWidth(),card.size.getWidth());
		card.redraw();
		grpHand.layout();
		card.redraw();
		
	}
	public void removeFromHand(String cardID){
		if(hasCardLoaded(cardID)){
			findCardById(cardID).dispose();
			hand.remove(cardID);
		}
	}
	
	public void addToField(String cardID){
		if(!hasCardLoaded(cardID))
			createCard(cardID,GameZone.FIELD);
		ClientCardInstance card = findCardById(cardID);
		field.add(cardID);
		card.setZone(GameZone.FIELD);
		Group g = findCardById(cardID).getController().equals(main.getPID())?group_1:this.grpEnemy;
		card.setParent(g);
		card.setBounds(g.getBounds().x,g.getBounds().y,card.size.getWidth(),card.size.getWidth());
		card.redraw();
		g.layout();
		card.redraw();
	}
	public void removeFromField(String cardID){
		if(hasCardLoaded(cardID))
			findCardById(cardID).dispose();
			field.remove(cardID);
	}
	public void manageField(){
		
	}
	public void manageHand(){
		//Try to beautify the shit out of dis hand
		
		
	}
	public void manageStack(){
		
	}
	
	public void disposeCard(String cardID){
		findCardById(cardID).dispose();
		cards.remove(findCardById(cardID));
	}
	
	public boolean hasCardLoaded(String id){
		return (findCardById(id)!=null);
	}
	public ClientCardInstance findCardById(String id){
		for(ClientCardInstance c:cards){
			if(id.equals(String.valueOf(c.getID())))
					return c;
		}
		return null;
	}
	public GameZones getPosition(String cardID){
		return null;
	}
	public void moveCard(String cardID, String zone){
		if(!hasCardLoaded(cardID)) {
			System.out.println("KDFj;afj");
			main.sendData("GETCARDINFO;"+this.getID()+";"+cardID);
		}
		switch(GameZones.valueOf(zone)){
			case HAND:
				addToHand(cardID); break;
			case FIELD:
				addToField(cardID);
				break;
			case STACK:
				addToStack(cardID); break;
			case GRAVEYARD:
				findCardById(cardID).dispose(); break;
			default: 
				break;
		}
	}
	
	
	public void createCard(String cardID){
		System.out.println("ke");
		//CardTemplate template = CardTemplateManager.get().GetCardTemplate(Integer.valueOf(templateID));
		ClientCardInstance card = new ClientCardInstance(this, Integer.valueOf(cardID), main, cardID,this);
		//card.SetTemplate(Integer.valueOf(templateID));
		cards.add(card);
	}
	public void createCard(String cardID, String zone){
		ClientCardInstance card=null;
		switch(GameZone.valueOf(zone)){
		case HAND:
			card = new ClientCardInstance(grpHand, Integer.valueOf(cardID), main, cardID,this);
			card.setBounds(grpHand.getBounds().x,grpHand.getBounds().y,card.size.getWidth(),card.size.getWidth());
			handPos.x+=50;
			redraw();
			break;
		case FIELD:
			Group g = findCardById(cardID).getController().equals(main.getPID())?group_1:this.grpEnemy;
			card = new ClientCardInstance(g, Integer.valueOf(cardID), main, cardID,this);
			redraw();
			break;
		case STACK:
			card = new ClientCardInstance(grpStack, Integer.valueOf(cardID), main, cardID,this);
			break;
		
		default:break;
		}
		card.setZone(GameZone.valueOf(zone));
		cards.add(card);
		
	}
	public void createCard(String cardID, GameZone zone){
		ClientCardInstance card = new ClientCardInstance(this, Integer.valueOf(cardID), main, cardID,this);
		card.setZone(zone);
		cards.add(card);
	}
	public void setCard(String templateID, String cardID, String owner, String controller){
		System.out.println("hue");
		if(hasCardLoaded(cardID)){
			//CardTemplateManager.get().GetCardTemplate(Integer.valueOf(templateID));
			findCardById(cardID).setTemplate(templateID);
			findCardById(cardID).setOwner(owner);
			findCardById(cardID).setController(controller);
			/*
			 * update card
			 */
			if(findCardById(cardID).zone==ClientCardInstance.GameZone.FIELD)
				findCardById(cardID).setParent(findCardById(cardID).getController().equals(main.getPID())?group_1:this.grpEnemy);
			findCardById(cardID).redraw();
		}
	}
	
	public void setResource(String player, String res, String val) {
		if(player.equals(main.getPID())){
			switch(GameResources.valueOf(res)){
			case METAL:
				lblMetal.setText(val);
				break;
			case ENERGY:
				lblEnergy.setText(val);
				break;
			case TECH:
				lblTech.setText(val);
				break;
			default: break;
			}
		} else if(player.equals(eID)){
			switch(GameResources.valueOf(res)){
			case METAL:
				lblEmetal.setText(val);
				break;
			case ENERGY:
				lblEenergy.setText(val);
				break;
			case TECH:
				lblEtech.setText(val);
				break;
			default: break;
			}
		}
		
	}
	
	public void setActive(){
		this.active=active?false:true;
	}
	public void setActive(boolean t){
		active=t;
		if(active)
			this.enablePass();
		else
			this.disablePass();
	}
	public boolean getActive(){
		return active;
	}
	public void setWaiting(){
		waiting = waiting?false:true;
	}
	public void setWaiting(boolean t){
		waiting = t;
	}
	public boolean getWaiting(){
		return waiting;
	}
	public void setViewer(String cardID){
		if(hasCardLoaded(cardID)){
			findCardById(cardID).template.Render(vcgc, CardRenderSize.LARGE, findCardById(cardID).getStatBlock(),findCardById(cardID).getDamageTaken());
			vcard = cardID;
		}
		if(targeting){
			main.sendData("ATTACK;"+this.ID+";"+curattacker+";"+cardID);
			System.out.println("Attacking!");
			targeting = false;
			curattacker="";
			this.setAssistText("");
		}
	}

	public void setAssistText(String s){
		this.lblActionhelp.setText(s);
	}
	
	public void setPlayerState(String pid, String state) {
		switch(GamePlayer.PlayerStates.valueOf(state)){
		
		case JOINED:
			System.out.println("NOW IS " + state);
			if(pid.equals(main.getPID())){
				main.sendData(main.getDeck());
			} break;
		case READY:
			System.out.println("NOW IS " + state);
			//this.enablePass();
			break;
		case ACTIVE:
			System.out.println("NOW IS " + state);
			setActive(true);
			setWaiting(false);
			break;
		case WAITING:
			System.out.println("NOW IS " + state);
			setActive(false);
			setWaiting(true);
			break;
		case DONE:
			System.out.println("NOW IS " + state);
			setActive(false);
			break;
		case READING:
			this.enablePass();
			break;
		default:
			System.out.println("NOW IS " + state + "(NOT HANDLED)"); break;
		}
		
	}
}
