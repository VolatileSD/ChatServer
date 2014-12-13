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

   private final ActorRef roomManager;

   public ChatServerApplication(ActorRef roomManager) {
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
