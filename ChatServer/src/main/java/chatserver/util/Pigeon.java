package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

public class Pigeon{
	final ActorRef to;

	public Pigeon(ActorRef to){
		this.to = to;
	}

	public Object carry(MsgType type){
		return carry(type, null);
	}

	public Object carry(MsgType type, Object content){
		Object res = null;
		try{
			Actor<Msg, Object> actor = new BasicActor<Msg, Object>() {
	            @Override
	            protected Object doRun() throws InterruptedException, SuspendExecution {
	                to.send(new Msg(type, self(), content));
	                Msg reply = receive();
	                while(reply.getType() != type) reply = receive(); 
	                return reply.getContent();
	            }
	        };
	        actor.spawn();
	        res = actor.get();
	    } catch (Exception e){ System.out.println(e.getMessage()); }

        return res;
	}

}