package chatserver.gui;

import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.dialog.MessageBox;

public class AdminGUI extends Window {
   /* CODIGO QUE ESTAVA NA MAIN
    GUIScreen guiScreen = TerminalFacade.createGUIScreen();
    if(guiScreen == null) {
    System.err.println("Couldn't allocate a terminal!");
    return;
    }
    guiScreen.getScreen().startScreen();
    guiScreen.setTitle("GUI Test");
    
    AdminGUI myWindow = new AdminGUI();
    guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);

    //Do GUI logic here

    guiScreen.getScreen().stopScreen();
    */

   public AdminGUI() {
      super("My Window!");
      Panel horisontalPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
      Panel leftPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
      Panel middlePanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
      Panel rightPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);

      horisontalPanel.addComponent(leftPanel);
      horisontalPanel.addComponent(middlePanel);
      horisontalPanel.addComponent(rightPanel);

      addComponent(horisontalPanel);
      addComponent(new Label("This is the second label, red", Terminal.Color.RED));
      addComponent(new Label("This is the third label, fixed 50 columns", 50));
      addComponent(new Label("This is the last label\nSpanning\nMultiple\nRows"));
      addComponent(new Button("Button with action", new Action() {
         @Override
         public void doAction() {
            MessageBox.showMessageBox(getOwner(), "Hello", "You selected the button with an action attached to it!");
         }
      }));
   }
}
