package Client;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class StoreUI extends GameStateUI {
    public StoreUI(Shell client, ClientMain main) {
        m_Host = main;
        m_UIObjects = new Vector<Control>();
        final Label storeTitle = new Label(client, SWT.READ_ONLY);
        storeTitle.setAlignment(SWT.CENTER);
        storeTitle.setBounds(250, 0, 100, 50);
        storeTitle.setText("ISTCG Store");

        // final Text BOOST_ONE_T = new Text(client, SWT.BORDER | SWT.WRAP
        // | SWT.SINGLE);
        final Button BOOST_ONE_B = new Button(client, SWT.PUSH);
        // final Text BOOST_TWO_T = new Text(client, SWT.BORDER | SWT.WRAP
        // | SWT.SINGLE);
        final Button BOOST_TWO_B = new Button(client, SWT.PUSH);
        // final Text BOOST_THREE_T = new Text(client, SWT.BORDER | SWT.WRAP
        // | SWT.SINGLE);
        final Button BOOST_THREE_B = new Button(client, SWT.PUSH);

        BOOST_ONE_B.setText("Facton 1");
        BOOST_ONE_B.addMouseListener(new MouseListener() {

            @Override
            public void mouseDoubleClick(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseUp(MouseEvent arg0) {
                // TODO Auto-generated method stub
                // main.changeState(GameState.STORE);
                // main.changeState(GameState.GAME);
                System.out.println("Not yet Implemented");
            }

        });
        BOOST_TWO_B.setText("Facton 2");
        BOOST_TWO_B.addMouseListener(new MouseListener() {

            @Override
            public void mouseDoubleClick(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseUp(MouseEvent arg0) {
                // TODO Auto-generated method stub
                // main.changeState(GameState.STORE);
                // main.changeState(GameState.GAME);
                System.out.println("Not yet Implemented");
            }

        });
        BOOST_THREE_B.setText("Facton 2");
        BOOST_THREE_B.addMouseListener(new MouseListener() {

            @Override
            public void mouseDoubleClick(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseUp(MouseEvent arg0) {
                // TODO Auto-generated method stub
                // main.changeState(GameState.STORE);
                // main.changeState(GameState.GAME);
                System.out.println("Not yet Implemented");
            }

        });
        /*
         * Add more stuff here later, will need to implement points and shit.
         */

        m_UIObjects.add(BOOST_ONE_B);
        m_UIObjects.add(BOOST_TWO_B);
        m_UIObjects.add(BOOST_THREE_B);
        m_UIObjects.add(storeTitle);
        for (Control w : m_UIObjects) {
            w.setVisible(false);
        }
    }

    public int getWidth() {
        return this.getWidth();
    }

    public int getHeight() {
        return this.getHeight();
    }

    @Override
    public void Disable() {
        for (Control w : m_UIObjects) {
            w.setVisible(false);
        }
    }

    @Override
    public void Enable() {
        for (Control w : m_UIObjects) {
            w.setVisible(true);
        }
    }

    @Override
    public void HandleMessage(String[] inputs) {

    }
}
