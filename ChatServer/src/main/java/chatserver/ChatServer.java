package chatserver;

import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.ChatServerApplication;
import chatserver.quasar.Acceptor;
import chatserver.quasar.RoomManager;
import chatserver.gui.AdminGUI;
import chatserver.rest.entities.Admin;


import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.GUIScreen.Position;
import com.googlecode.lanterna.TerminalFacade;



public class ChatServer {
  public static void main(String[] args) throws Exception {
    int port = 1111; //Integer.parseInt(args[0]);
    ActorRef roomManager = new RoomManager().spawn();
    new ChatServerApplication(roomManager).run(args); // starts rest
    Acceptor acceptor = new Acceptor(port, roomManager);
    Admin ad= new Admin();
    ad.addRoomRequest("randomroom");
    ad.listRoomsRequest();

    /*
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
    acceptor.spawn();
    acceptor.join();
    
    
    
    
    
    
  }
}