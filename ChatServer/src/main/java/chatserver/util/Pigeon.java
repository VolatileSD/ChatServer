package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

public class Pigeon{
	ActorRef to;

	public Pigeon(ActorRef to){
		this.to = to;
	}

	public Object carry(MsgType type){
		return carry(type, null);
	}

	public Object carry(MsgType type, Object o){
		Object res = null;
		try{
			Actor<Msg, Object> actor = new BasicActor<Msg, Object>() {
	            @Override
	            protected Object doRun() throws InterruptedException, SuspendExecution {
	                to.send(new Msg(type, self(), o));
	                Msg reply = receive();
	                while(reply.getType() != type) reply = receive(); 
	                return reply.getO();
	            }
	        };
	        actor.spawn();
	        res = actor.get();
	    } catch (Exception e){ System.out.println(e.getMessage()); }

        return res;
	}

}