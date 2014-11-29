package hello.health;

import com.codahale.metrics.health.HealthCheck;

public class TemplateHealthCheck extends HealthCheck {
    private final String template;

    public TemplateHealthCheck(String template) { this.template = template; }

    @Override
    protected Result check() throws Exception {
        return String.format(template, "TEST").contains("TEST") ?
               Result.healthy() :
               Result.unhealthy("template doesn't include a name");
    }
}

