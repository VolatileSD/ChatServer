package chatserver.rest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.resource.RoomResource;
import chatserver.rest.resource.RoomsResource;
import chatserver.rest.health.ChatServerHealthCheck;
import chatserver.rest.entity.Rooms;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerApplication extends Application<ChatServerConfiguration> {

   private final ActorRef manager;
   private final ActorRef roomManager;

   public ChatServerApplication(ActorRef manager, ActorRef roomManager) {
      this.manager = manager;
      this.roomManager = roomManager;
   }

   @Override
   public String getName() {
      return "ChatServer";
   }

   @Override
   public void initialize(Bootstrap<ChatServerConfiguration> bootstrap) {
   }

   @Override
   public void run(ChatServerConfiguration configuration, Environment environment) {
      Msg msg;
      try {
         msg = new Pigeon(manager).carry(MsgType.RESTORE);
         roomManager.send(msg);
      } catch (InterruptedException | SuspendExecution | ExecutionException e) {
         Logger.getLogger(ChatServerApplication.class.getName()).log(Level.SEVERE, "Error trying to restore - {0}", e.getMessage());
         return;
      }
      Rooms rooms = new Rooms((Map) msg.getContent());
      environment.jersey().register(new RoomsResource(rooms));
      environment.jersey().register(new RoomResource(rooms, roomManager));
      environment.healthChecks().register("ChatServer", new ChatServerHealthCheck());
      /*
       final HttpClient httpClient = new HttpClientBuilder(environment).using(config.getHttpClientConfiguration())
       .build();
       environment.addResource(new ExternalServiceResource(httpClient));
       */
   }
}
