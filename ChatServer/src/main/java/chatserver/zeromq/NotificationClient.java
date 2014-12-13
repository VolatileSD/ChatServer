package chatserver.zeromq;

import org.zeromq.ZMQ;

public class NotificationClient {

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
        ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
        xpub.bind("tcp://*:" + args[0]);
        xsub.bind("tcp://*:" + args[1]);
        ZMQ.proxy(xpub, xsub, null);
    }
}

class Client {

    public static void main(String[] args) throws Exception {
        new CPub(args[1], args[0]).start();
    }

    static class CPub extends Thread {
        String xsub;
        String xpub;

        CPub(String xsub, String xpub) {
            this.xsub = xsub;
            this.xpub = xpub;
        }

        @Override
        public void run() {
            String room = "";
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket pub = context.socket(ZMQ.PUB);
            ZMQ.Socket cr = context.socket(ZMQ.PUB);
            pub.connect("tcp://localhost:" + this.xsub);
            cr.bind("inproc://cpubsub");
            new CSub(this.xpub, context).start();
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
                    pub.send(s);
                }

            }
            cr.close();
            pub.close();
            context.term();
        }
    }

    static class CSub extends Thread {

        String xpub;
        ZMQ.Context context;

        CSub(String xpub, ZMQ.Context context) {
            this.xpub = xpub;
            this.context = context;
        }

        @Override
        public void run() {
            String room = "";
            ZMQ.Socket socket = this.context.socket(ZMQ.SUB);
            socket.connect("tcp://localhost:" + this.xpub);
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
