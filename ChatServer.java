import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.*;
import java.net.InetSocketAddress;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import java.util.*;

public class ChatServer {

  static int MAXLEN = 1024;

  static enum Type { DATA, EOF, IOE, ENTER, LEAVE, LINE, LOGIN, LOGIN_OK, CREATE, CREATE_OK, USER_EXISTS, WRONG_PW } //IOE: ioexception . 3 primeiras entre LineReader e entre o User
  static class Msg {
    final Type type;
    final Object o;  // careful with mutable objects, such as the byte array
    // quando mando uma linha para toda a gente mando um byte array e no cliente que vai escrever para o seu socket
    // vai fazer wrap deste byte array para um bytebyffer
    // se passasse o bytebuffer para todos os clientes ia ter problemas com os apontadores do bytebuffer
    // e assim evito fazer duplicate e partilho a mensagem para todos
    // poupo memoria
    // smart!

    Msg(Type type, Object o) { this.type = type; this.o = o; }
  }

  static class Login {
    final String username;
    final String password;
    final ActorRef<Msg> dest;
    Login(String username, String password, ActorRef<Msg> dest){ this.username = username; this.password = password; this.dest = dest; }
  }
  
  static class LineReader extends BasicActor<Msg, Void> {
    final ActorRef<Msg> dest;
    final FiberSocketChannel socket;
    ByteBuffer in = ByteBuffer.allocate(MAXLEN);
    ByteBuffer out = ByteBuffer.allocate(MAXLEN);

    LineReader(ActorRef<Msg> dest, FiberSocketChannel socket) {
      this.dest = dest; this.socket = socket;
    }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      boolean eof = false;
      byte b = 0;
      try {
        for(;;) {
          if (socket.read(in) <= 0) eof = true;
          in.flip();
          while(in.hasRemaining()) {
            b = in.get();
            out.put(b);
            if (b == '\n') break;
          }
          if (eof || b == '\n') { // send line
            out.flip();
            if (out.remaining() > 0) {
              byte[] ba = new byte[out.remaining()];
              out.get(ba);
              out.clear();
              dest.send(new Msg(Type.DATA, ba));
            }
          }
          if (eof && !in.hasRemaining()) break;
          in.compact(); // prepara o buffer para escrever à frente
          // no caso em que recebo mais do que uma linha?!?
        }
        dest.send(new Msg(Type.EOF, null));
        return null;
      } catch (IOException e) {
        dest.send(new Msg(Type.IOE, null));
        return null;
      }
    }

  }

  static class User extends BasicActor<Msg, Void> {
    final ActorRef lm;
    final ActorRef room;
    final FiberSocketChannel socket;
    boolean login_ok;
    String username;
    User(ActorRef lm, ActorRef room, FiberSocketChannel socket) { 
      this.lm = lm; this.room = room; this.socket = socket; login_ok = false; 
      this.username = "";
    }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      String wp = "Wrong password!\n";
      String ue = "User Exists!\n";
      String lo = "Login OK!\n";
      String co = "Create OK!\n";
      String usage = "Usage:\ncreate(user,password).\nlogin(user,password).\n";

      try{ socket.write(ByteBuffer.wrap(usage.getBytes())); }
      catch(IOException e) { return null; }

      new LineReader(self(), socket).spawn();

      while (receive(msg -> {
        try {
        switch (msg.type) {
          case DATA:
            String line = new String((byte[])msg.o);
            if(line.startsWith("login(") && line.endsWith(").\n")){
              String[] up = line.substring(6).split(",");
              username = up[0];
              Login login = new Login(up[0], up[1].substring(0, up[1].length()-3), self());
              lm.send(new Msg(Type.LOGIN, login));
            }
            else if(line.startsWith("create(") && line.endsWith(").\n")){
              String[] up = line.substring(7).split(",");
              Login login = new Login(up[0], up[1].substring(0, up[1].length()-3), self());
              lm.send(new Msg(Type.CREATE, login));
            }
            else socket.write(ByteBuffer.wrap(usage.getBytes()));
            return true;
          case EOF:
          case IOE:
            room.send(new Msg(Type.LEAVE, self()));
            socket.close();
            return false;
          case LINE:
            socket.write(ByteBuffer.wrap((byte[])msg.o));
            return true;
          case CREATE_OK:
            socket.write(ByteBuffer.wrap(co.getBytes()));
            return true;
          case LOGIN_OK:
            socket.write(ByteBuffer.wrap(lo.getBytes()));
            rm.send(new Msg(Type.ENTER, self()));
            connectRoom();
            return true;
          case WRONG_PW:
            socket.write(ByteBuffer.wrap(wp.getBytes()));
            return true;
          case USER_EXISTS:
            socket.write(ByteBuffer.wrap(ue.getBytes()));
            return true;
        }
        } catch (IOException e) {
          socket.close();
          room.send(new Msg(Type.LEAVE, self()));
        }
        return false;  // stops the actor if some unexpected message is received
      }));
      return null;
    }

    private void connectRoom() throws InterruptedException, SuspendExecution {
      while(receive(msg -> {
        try{
        switch(msg.type){
          case LINE:
            socket.write(ByteBuffer.wrap(((String) msg.o).getBytes()));
            return true;
          case DATA:

            return true;
          case EOF:
          case IOE:
            socket.close();
            return false
        }
        } catch(IOException e) { socket.close(); }
        return false;
      }));
      return null;
    }

    private void running() throws InterruptedException, SuspendExecution {
      while(receive(msg -> {
        try{
        switch(msg.type){
         case DATA:
           String line = new String((byte[])msg.o);
           room.send(new Msg(Type.LINE, (username + ":" + line).getBytes()));
        }
        } catch (IOException e){}
        return false;
      }));
      return null;
    }
  }

  static class RoomManager extends BasicActor<Msg, Void> {
    String usage = "Usage:\ncreate(room_name).\nconnect(room_name)\nType rooms(). to find out the available rooms.\n";
    private Map<String, Room> rooms = new HashMap<String, Room>();

    protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
        try{
        switch (msg.type) {
          case ENTER:
            socket.write(ByteBuffer.wrap(usage.getBytes()));
            return true;
    //      case 



        }
        } catch(IOException e) {}
        return false;
      }));
      return null;
    }

  }


  static class Room extends BasicActor<Msg, Void> {
    private Set<ActorRef> users = new HashSet();

    protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
        switch (msg.type) {
          case ENTER:
            users.add((ActorRef)msg.o);
            return true;
          case LEAVE:
            users.remove((ActorRef)msg.o);
            return true;
          case LINE:
            for (ActorRef u : users) u.send(msg);
            return true;
        }
        return false;
      }));
      return null;
    }
  }

  static class LoginManager extends BasicActor<Msg, Void> {
    private Map<String, String> reg = new HashMap<String, String>();

    protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
        Login login = (Login) msg.o;
        switch (msg.type) {
          case LOGIN:
            if(reg.get(login.username).equals(login.password)){
              login.dest.send(new Msg(Type.LOGIN_OK, null));
            }
            else login.dest.send(new Msg(Type.WRONG_PW, null));
            return true;
          case CREATE:
            if(reg.containsKey(login.username)) login.dest.send(new Msg(Type.USER_EXISTS, null));
            else{
              reg.put(login.username, login.password);
              login.dest.send(new Msg(Type.CREATE_OK, null));
            }
            return true;
        }
        return false;
      }));
      return null;
    }
  }

  static class Acceptor extends BasicActor {
    final int port;
    final ActorRef lm;
    final ActorRef rm;
    Acceptor(int port, ActorRef room, ActorRef lm, ActorRef rm) { this.port = port; this.lm = lm; this.rm = rm; }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      try {
      FiberServerSocketChannel ss = FiberServerSocketChannel.open();
      ss.bind(new InetSocketAddress(port));
      while (true) {
        FiberSocketChannel socket = ss.accept();
        new User(lm, rm, socket).spawn();
      }
      } catch (IOException e) { }
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    int port = 12345; //Integer.parseInt(args[0]);
    ActorRef loginManager = new LoginManager().spawn();
    ActorRef roomManager = new RoomManager().spawn();
    Acceptor acceptor = new Acceptor(port, loginManager roomManager);
    acceptor.spawn();
    acceptor.join();
  }

}
