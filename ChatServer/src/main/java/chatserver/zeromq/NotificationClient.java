package chatserver.zeromq;

import org.zeromq.ZMQ;

public class NotificationClient {

    public static void main(String[] args) {
        new ConsoleReader(args[0]).start();
    }

    static class ConsoleReader extends Thread {
        String xpub;

        ConsoleReader(String xpub) {
            this.xpub = xpub;
        }

        @Override
        public void run() {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket cr = context.socket(ZMQ.PUB);
            cr.bind("inproc://cpubsub");
            new Subscriber(this.xpub, context).start();
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
                    cr.sendMore(":sub");
                    cr.send(s.substring(5));
                }

            }
            cr.close();
            context.term();
        }
    }

    static class Subscriber extends Thread {
        String xpub;
        ZMQ.Context context;

        Subscriber(String xpub, ZMQ.Context context) {
            this.xpub = xpub;
            this.context = context;
        }

        @Override
        public void run() {
            ZMQ.Socket socket = this.context.socket(ZMQ.SUB);
            socket.connect("tcp://localhost:" + this.xpub);
            socket.connect("inproc://cpubsub");
            socket.subscribe(":sub".getBytes());            
            socket.subscribe(":unsub".getBytes());

            while (true) {
                byte[] first = socket.recv();
                String firstS = new String(first);
                byte[] second = socket.recv();
                
                if (firstS.equals(":sub")) socket.subscribe(second);
                else if (firstS.equals(":unsub")) socket.unsubscribe(second);
                else System.console().writer().println(new String(second));
            }
        }
    }
}
