package chatserver.rest.health;

import com.codahale.metrics.health.HealthCheck;

public class ChatServerHealthCheck extends HealthCheck {

    public ChatServerHealthCheck() {
    }

    @Override
    protected Result check() throws Exception {
        return (1 == 1) ?
               Result.healthy() :
               Result.unhealthy("See useful health checks that can be done");
    }
}

