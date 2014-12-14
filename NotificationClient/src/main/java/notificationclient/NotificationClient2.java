package notificationclient;

import org.zeromq.ZMQ;

public class NotificationClient2 {

   private static final String port = "2222";
   private static final String internalPort = "3333";

   public static void main(String[] args) throws Exception {
      new Proxy().start();
      new CPub().start();
   }

   static class Proxy extends Thread {

      @Override
      public void run() {
         ZMQ.Context context = ZMQ.context(1);
         ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
         ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
         xpub.bind("tcp://*:" + port);
         xsub.bind("tcp://*:" + internalPort);
         ZMQ.proxy(xpub, xsub, null);
      }
   }

   static class CPub extends Thread {

      @Override
      public void run() {
         String room = "";
         ZMQ.Context context = ZMQ.context(1);
         ZMQ.Socket pub = context.socket(ZMQ.PUB);
         ZMQ.Socket cr = context.socket(ZMQ.PUB);
         pub.connect("tcp://localhost:" + internalPort);
         cr.bind("inproc://cpubsub");
         new CSub(context).start();
         System.out.println("use the follow to connect/change room:\n:cr room\n");
         while (true) {
            String s = System.console().readLine();
            if (s == null) {
               break;
            } else if (s.startsWith(":cr ")) {
               room = ":msg " + s.substring(4);
               cr.sendMore(":cr");
               cr.send(room);
            } else {
               pub.sendMore(room);
               pub.send(": " + s);
            }

         }
         cr.close();
         pub.close();
         context.term();
      }
   }

   static class CSub extends Thread {
      ZMQ.Context context;

      CSub(ZMQ.Context context) {
         this.context = context;
      }

      @Override
      public void run() {
         String room = "";
         ZMQ.Socket socket = this.context.socket(ZMQ.SUB);
         socket.connect("tcp://localhost:" + port);
         socket.connect("inproc://cpubsub");
         socket.subscribe(":cr".getBytes());

         while (true) {
            byte[] b = socket.recv();
            String s = new String(b);
            if (s.equals(":cr")) {
               if (!"".equals(room)) {
                  socket.unsubscribe(room.getBytes());
               }
               byte[] bb = socket.recv();
               room = new String(bb);
               socket.subscribe(room.getBytes());
            } else {
               byte[] bb = socket.recv();
               System.out.println(new String(bb));
            }
         }
      }
   }
}
