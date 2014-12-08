package chatserver;

import chatserver.rest.ChatServerApplication;
import chatserver.quasar.Acceptor;

public class ChatServer{
  public static void main(String[] args) {
    try{
      int port = 12345; //Integer.parseInt(args[0]);
      new ChatServerApplication().run(args); // starts rest
      Acceptor acceptor = new Acceptor(port);
      acceptor.spawn();
      acceptor.join();  
    } catch(Exception e){ System.out.println(e.getMessage()); }
  }  
}