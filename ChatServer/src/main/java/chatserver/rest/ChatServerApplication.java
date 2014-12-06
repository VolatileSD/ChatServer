package chatserver.rest;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import chatserver.rest.resources.RoomResource;
import chatserver.rest.health.ChatServerHealthCheck;


public class ChatServerApplication extends Application<ChatServerConfiguration> {
    public static void main(String[] args) throws Exception {
        new ChatServerApplication().run(args);
    }

    @Override
    public String getName() { return "ChatServer"; }

    @Override
    public void initialize(Bootstrap<ChatServerConfiguration> bootstrap) { }

    @Override
    public void run(ChatServerConfiguration configuration, Environment environment) {
        environment.jersey().register(new RoomResource());
        environment.healthChecks().register("ChatServer", new ChatServerHealthCheck());
    }

}