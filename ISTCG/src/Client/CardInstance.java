package Client;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import Client.CardTemplate.CardRenderSize;
import Shared.NetworkObject;
import Shared.StatBlock;

public class CardInstance implements NetworkObject {
    private int m_NetworkObjectID;

    private String m_CardName;
    private String m_CardText;
    private ArrayList<StatBlock> m_Stats;
    private CardRenderSize m_CurrentSize = CardRenderSize.TINY;
    private CardRenderSize m_LastSize;

    private Display display = null;
    private Shell shell = null;
    private Canvas canvas = null;
    private GC gc = null;
    private CardTemplate m_CardTemplate = null;

    // private B

    public void SetTemplate(int template_id) {
        m_CardTemplate = CardTemplateManager.get().GetCardTemplate(template_id);
    }

    public CardInstance(Composite game, int cardID) {
        // this.display=display;
        // shell = new Shell(display);
        // canvas = new Canvas(shell, 0);
        Canvas canvas = new Canvas(game, SWT.NONE);
        gc = new GC(canvas);
        SetNetworkID(cardID);
        // SetCardName(cardname);
        // SetCardText(cardtext);
        /*
         * int len = stuff.length; m_Stats = new StatBlock[len]; for (int i = 0;
         * i < len; i++) { m_Stats[i] = stuff[i]; }
         */

        // render
        RenderCard(gc, CardRenderSize.LARGE, m_Stats);

        canvas.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent arg0) {
                // TODO Auto-generated method stub
                switch (m_CurrentSize) {
                default:
                    RenderCard(gc, m_CurrentSize, m_Stats);
                    // Potentially render surrounding cards to stack accordingly
                    break;
                case LARGE:
                    Point p = Display.getCurrent().getCursorLocation();
                    if (225 < p.y && p.y < 267) {
                        if (10 <= p.x && p.x < 55) {

                        }
                        else if (55 <= p.x && p.x < 90) {

                        }
                    }

                    break;
                }
            }

            @Override
            public void mouseDown(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDoubleClick(MouseEvent arg0) {
                // TODO Auto-generated method stub
                switch (m_CurrentSize) {
                default:
                    RenderCard(gc, CardRenderSize.LARGE, m_Stats);
                    break;
                case LARGE:
                    RenderCard(gc, m_LastSize, m_Stats);
                    break;
                }
            }
        });

    }

    /*
     * private int getMouseX(Point p) { // TODO stuffs return p.x;
     * 
     * } private int getMouseY(Point p) { // TODO stuffs return -1;
     * 
     * }
     */

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

    // Renders Card
    public void RenderCard(GC targetGC, CardRenderSize size,
            ArrayList<StatBlock> stats) {

        this.Render(targetGC, size, stats);
    }

    private void Render(GC targetGC, CardRenderSize size,
            ArrayList<StatBlock> stats) {

        Label m_BGButton = new Label(canvas, 0);
        gc = targetGC;
        if (m_CurrentSize != size) {
            if (!(m_CardTemplate == null)) {
                m_CardTemplate.Render(targetGC, size, stats);
            }
            else {
                int width = size.getWidth();
                int height = size.getHeight();
                targetGC.fillRectangle(0, 0, width, height);
            }
            m_LastSize = m_CurrentSize;
            m_CurrentSize = size;
        }
        switch (size) {
        default:
            break;

        case LARGE:
            // creates name box
            /*
             * gc.drawImage(textL, 10, 10); gc.drawImage(textBL, 10, 31); for
             * (int i = 31; i < 300; i += 21) { gc.drawImage(textM, i, 10);
             * gc.drawImage(textB, i, 31);
             * 
             * } gc.drawText(name, 25, 20);
             * 
             * break;
             */
        }
    }

    // OLD RENDERER
    /*
     * public void SetCanvas() { // loading images Image cardBL = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/card_border_corner_bottom_left.png"); Image cardBR = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/card_border_corner_bottom_right.png"); Image cardTL = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/card_border_corner_top_left.png"); Image cardTR = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/card_border_corner_top_right.png"); Image cardL = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/card_border_edge_left.png"); Image cardR = new Image(display,
     * System.getProperty("user.dir") + "/data/card_border_edge_right.png");
     * Image cardT = new Image(display, System.getProperty("user.dir") +
     * "/data/card_border_edge_top.png"); Image cardB = new Image(display,
     * System.getProperty("user.dir") + "/data/card_border_edge_bottom.png");
     * Image textBL = new Image(display, System.getProperty("user.dir") +
     * "/data/text_box_corner_bottom_left.png"); Image textBR = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/text_box_corner_bottom_right.png"); Image textTL = new
     * Image(display, System.getProperty("user.dir") +
     * "/data/text_box_corner_top_left.png"); Image textTR = new Image(display,
     * System.getProperty("user.dir") + "/data/text_box_corner_top_right.png");
     * Image textL = new Image(display, System.getProperty("user.dir") +
     * "/data/text_box_edge_left.png"); Image textR = new Image(display,
     * System.getProperty("user.dir") + "/data/text_box_edge_righ.png"); Image
     * textT = new Image(display, System.getProperty("user.dir") +
     * "/data/text_box_edge_top.png"); Image textB = new Image(display,
     * System.getProperty("user.dir") + "/data/text_box_edge_bottom.png"); Image
     * textM = new Image(display, System.getProperty("user.dir") +
     * "/data/text_box_body.png"); // the Image back was intended as the
     * background image, not the textbox body. // the text box body is Image
     * textM (i would have used textB, but that is the bottom of the text box)
     * String png = System.getProperty("user.dir") + "/data/"+"text_box_body" +
     * ".png"; Image back = new Image(display, png);
     * 
     * 
     * // prepare canvas canvas.setSize(300, 400); gc.drawImage(back, 0, 0); //
     * creates name box gc.drawImage(textL, 10, 10); gc.drawImage(textBL, 10,
     * 31); for (int i = 31; i < 300; i += 21) { gc.drawImage(textM, i, 10);
     * gc.drawImage(textB, i, 31);
     * 
     * } gc.drawText(m_CardName, 25, 20);
     * 
     * // creates text box bg gc.drawImage(textTL, 10, 250); for (int i = 31; i
     * < 300; i += 21) { gc.drawImage(textT, i, 250); } for (int i = 271; i <
     * 400; i += 21) { gc.drawImage(textL, 10, i); for (int j = 31; j < 300; j
     * += 21) { gc.drawImage(textM, j, i); } } Label cardInfoWrap = new
     * Label(canvas, SWT.WRAP); cardInfoWrap.setText(m_CardText);
     * cardInfoWrap.setBounds(31, 271, 250, 110);
     * 
     * // draws main card outline gc.drawImage(cardTL, 0, 0);
     * gc.drawImage(cardTR, 275, 0); gc.drawImage(cardBL, 0, 375);
     * gc.drawImage(cardBR, 275, 375); for (int i = 25; i <= 274; i += 25) {
     * gc.drawImage(cardT, i, 0); gc.drawImage(cardB, i, 375); } for (int i =
     * 25; i <= 374; i += 25) { gc.drawImage(cardL, 0, i); gc.drawImage(cardR,
     * 275, i); } }
     */

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
        return m_Stats.get(i);
    }

    public void SetStat(StatBlock st, int i) {
        m_Stats.set(i, st);
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
