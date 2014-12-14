package notificationclient;

import org.zeromq.ZMQ;

public class NotificationClient {

   private static final int port = 2222;
   private static final ZMQ.Context context = ZMQ.context(1);

   public static void main(String[] args) {
      new ConsoleReader().start();
   }

   static class ConsoleReader extends Thread {

      @Override
      public void run() {
         ZMQ.Socket cr = NotificationClient.context.socket(ZMQ.PUB);
         cr.bind("inproc://cpubsub");
         new Subscriber().start();
         System.console().writer().println("To subscribe events type:\n:sub eventName");
         System.console().writer().println("To unsubscribe events type:\n:unsub eventName");

         while (true) {
            String s = System.console().readLine(); // if it's not used in a console it's a problem
            if (s == null) {
               break;
            } else if (s.startsWith(":sub ")) {
               cr.sendMore(":sub");
               cr.send(s.substring(5));
            } else if (s.startsWith(":unsub ")) {
               cr.sendMore(":unsub");
               cr.send(s.substring(7));
            }

         }
         cr.close();
         context.term();
      }
   }

   static class Subscriber extends Thread {

      @Override
      public void run() {
         ZMQ.Socket socket = NotificationClient.context.socket(ZMQ.SUB);
         socket.connect("tcp://localhost:" + NotificationClient.port);
         socket.connect("inproc://cpubsub");
         socket.subscribe(":sub".getBytes());
         socket.subscribe(":unsub".getBytes());

         while (true) {
            byte[] first = socket.recv();
            String firstS = new String(first);
            byte[] second = socket.recv();

            switch (firstS) {
               case ":sub":                  
                  System.out.println("!" + new String(second) + "!");
                  socket.subscribe(second);
                  break;
               case ":unsub":
                  System.out.println("!" + new String(second) + "!");
                     socket.unsubscribe(second);
                  break;
               default:
                  System.out.println(new String(second));
                  break;
            }
         }
      }
   }
}
