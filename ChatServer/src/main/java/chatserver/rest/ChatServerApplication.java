package chatserver.rest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.resource.RoomResource;
import chatserver.rest.resource.RoomsResource;
import chatserver.rest.health.ChatServerHealthCheck;
import chatserver.rest.entity.Rooms;
import java.util.Collection;

public class ChatServerApplication extends Application<ChatServerConfiguration> {

   private final ActorRef roomManager;
   private final Collection<String> roomsRestored;

   public ChatServerApplication(ActorRef roomManager, Collection<String> roomsRestored) {
      this.roomManager = roomManager;
      this.roomsRestored = roomsRestored;
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
     
      Rooms rooms = new Rooms(roomsRestored);
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
