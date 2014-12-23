package chatserver.rest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.resources.RoomResource;
import chatserver.rest.resources.RoomsResource;
import chatserver.rest.health.ChatServerHealthCheck;
import chatserver.rest.entities.Rooms;

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
      /*
       Msg msg;
       try {
       msg = new Pigeon(manager).carry(MsgType.RESTORE);
       } catch (InterruptedException | SuspendExecution | ExecutionException e) {
       Logger.getLogger(ChatServerApplication.class.getName()).log(Level.SEVERE, "Error trying to restore!{0}", e.getMessage());
       return;
       }
       Rooms rooms = new Rooms((Collection) msg.getContent());
       */
      Rooms rooms = new Rooms();
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
