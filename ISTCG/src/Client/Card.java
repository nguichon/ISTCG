package Client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import Shared.NetworkObject;
import Shared.StatBlock;

public class Card implements NetworkObject {
    private int m_NetworkObjectID;

    private String m_CardName;
    private String m_CardText;
    private StatBlock m_Stats[];
    // private int attack;
    // private int defense;
    // private int structure;
    // private int gear_points;
    // private int damage;
    // private int delay;
    // private int costA;
    // private int costB;
    // private int costC;

    // private Image m_CardImage;
    private Display display = null;
    private  Shell shell = null;
    private  Canvas canvas = null;
    private GC gc = null;

    public Card(Display display,int cardID, String cardname, String cardtext,
            StatBlock... stuff) {
    	this.display=display;
        shell = new Shell(display);
        canvas = new Canvas(shell, 0);
        gc = new GC(canvas);
        SetNetworkID(cardID);
        SetCardName(cardname);
        SetCardText(cardtext);
        int len = stuff.length;
        m_Stats = new StatBlock[len];
        for (int i = 0; i < len; i++) {
            m_Stats[i] = stuff[i];
        }
        SetCanvas();

    }

    @Override
    public int compareTo(NetworkObject arg0) {
        if (m_NetworkObjectID < arg0.GetNetworkID()) {
            return -1;
        }
        if (m_NetworkObjectID > arg0.GetNetworkID()) {
            return 1;
        }
        return 0;
    }

    @Override
    public void SetNetworkID(int newID) {
        m_NetworkObjectID = newID;
    }

    @Override
    public int GetNetworkID() {
        return m_NetworkObjectID;
    }

    @Override
    public boolean ParseMessage(String s) {
        return false;
    }

    public void SetCanvas() {
        // loading images
        Image cardBL = new Image(display, System.getProperty("user.dir") + "/data/card_border_corner_bottom_left.png");
        Image cardBR = new Image(display, System.getProperty("user.dir") + "/data/card_border_corner_bottom_right.png");
        Image cardTL = new Image(display, System.getProperty("user.dir") + "/data/card_border_corner_top_left.png");
        Image cardTR = new Image(display, System.getProperty("user.dir") + "/data/card_border_corner_top_right.png");
        Image cardL = new Image(display, System.getProperty("user.dir") + "/data/card_border_edge_left.png");
        Image cardR = new Image(display, System.getProperty("user.dir") + "/data/card_border_edge_right.png");
        Image cardT = new Image(display, System.getProperty("user.dir") + "/data/card_border_edge_top.png");
        Image cardB = new Image(display, System.getProperty("user.dir") + "/data/card_border_edge_bottom.png");
        Image textBL = new Image(display, System.getProperty("user.dir") + "/data/text_box_corner_bottom_left.png");
        Image textBR = new Image(display, System.getProperty("user.dir") + "/data/text_box_corner_bottom_right.png");
        Image textTL = new Image(display, System.getProperty("user.dir") + "/data/text_box_corner_top_left.png");
        Image textTR = new Image(display, System.getProperty("user.dir") + "/data/text_box_corner_top_right.png");
        Image textL = new Image(display, System.getProperty("user.dir") + "/data/text_box_edge_left.png");
        Image textR = new Image(display, System.getProperty("user.dir") + "/data/text_box_edge_righ.png");
        Image textT = new Image(display, System.getProperty("user.dir") + "/data/text_box_edge_top.png");
        Image textB = new Image(display, System.getProperty("user.dir") + "/data/text_box_edge_bottom.png");
        Image textM = new Image(display, System.getProperty("user.dir") + "/data/text_box_body.png");
        String png = System.getProperty("user.dir") + "/data/"+"text_box_body" + ".png";
        Image back = new Image(display, png);

        // prepare canvas
        canvas.setSize(300, 400);
        gc.drawImage(back, 0, 0);
        // creates name box
        gc.drawImage(textL, 10, 10);
        gc.drawImage(textBL, 10, 31);
        for (int i = 31; i < 300; i += 21) {
            gc.drawImage(textM, i, 10);
            gc.drawImage(textB, i, 31);

        }
        gc.drawText(m_CardName, 25, 20);

        // creates text box bg
        gc.drawImage(textTL, 10, 250);
        for (int i = 31; i < 300; i += 21) {
            gc.drawImage(textT, i, 250);
        }
        for (int i = 271; i < 400; i += 21) {
            gc.drawImage(textL, 10, i);
            for (int j = 31; j < 300; j += 21) {
                gc.drawImage(textM, j, i);
            }
        }
        Label cardInfoWrap = new Label(canvas, SWT.WRAP);
        cardInfoWrap.setText(m_CardText);
        cardInfoWrap.setBounds(31, 271, 250, 110);

        // draws main card outline
        gc.drawImage(cardTL, 0, 0);
        gc.drawImage(cardTR, 275, 0);
        gc.drawImage(cardBL, 0, 375);
        gc.drawImage(cardBR, 275, 375);
        for (int i = 25; i <= 274; i += 25) {
            gc.drawImage(cardT, i, 0);
            gc.drawImage(cardB, i, 375);
        }
        for (int i = 25; i <= 374; i += 25) {
            gc.drawImage(cardL, 0, i);
            gc.drawImage(cardR, 275, i);
        }
    }

    public Canvas Draw() {
        return canvas;
    }

    // "Get-ers" and "Set-ers"
    // m_CardName
    public String GetCardName() {
        return m_CardName;
    }

    public void SetCardName(String newName) {
        m_CardName = newName;
    }

    // m_CardText
    public String GetCardText() {
        return m_CardText;
    }

    public void SetCardText(String newText) {
        m_CardText = newText;
    }

    // m_Stats
    public StatBlock GetStat(int i) {
        return m_Stats[i];
    }

    public void SetStat(StatBlock st, int i) {
        m_Stats[i] = st;
    }

    // // Attack
    // public int getAttack() {
    // return attack;
    // }
    //
    // public void setAttack(int newAttack) {
    // attack = newAttack;
    // }
    //
    // // Defence
    // public int getDefence() {
    // return defence;
    // }
    //
    // public void setDefence(int newVal) {
    // defense = newVal;
    // }
    //
    // // Structure
    // public int getStructure() {
    // return structure;
    // }
    //
    // public void setStructure(int newVal) {
    // structure = newVal;
    // }
    //
    // // Gear_points
    // public int getGear_points() {
    // return gear_points;
    // }
    //
    // public void setGear_points(int newVal) {
    // gear_points = newVal;
    // }
    //
    // // Damage
    // public int getDamage() {
    // return damage;
    // }
    //
    // public void setDamage(int newVal) {
    // damage = newVal;
    // }
    //
    // // Delay
    // public int getDelay() {
    // return delay;
    // }
    //
    // public void setDelay(int newVal) {
    // delay = newVal;
    // }
    //
    // // CostA
    // public int getCostA() {
    // return costA;
    // }
    //
    // public void setCostA(int newVal) {
    // costA = newVal;
    // }
    //
    // // CostB
    // public int getCostB() {
    // return costB;
    // }
    //
    // public void setCostB(int newVal) {
    // costB = newVal;
    // }
    //
    // // CostC
    // public int getCostC() {
    // return costC;
    // }
    //
    // public void setCostC(int newVal) {
    // costC = newVal;
    // }

}
