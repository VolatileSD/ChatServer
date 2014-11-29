package hello;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import hello.resources.HelloResource;
import hello.health.TemplateHealthCheck;


public class HelloApplication extends Application<HelloConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloApplication().run(args);
    }

    @Override
    public String getName() { return "Hello"; }

    @Override
    public void initialize(Bootstrap<HelloConfiguration> bootstrap) { }

    @Override
    public void run(HelloConfiguration configuration,
                    Environment environment) {
        environment.jersey().register(
            new HelloResource(configuration.template, configuration.defaultName));
        environment.healthChecks().register("template",
            new TemplateHealthCheck(configuration.template));
            
    }
    


}

