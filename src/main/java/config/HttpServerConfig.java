package config;

import org.aeonbits.owner.Config;

@Config.Sources({"system:env", "classpath:config/httpserver.properties"})
public interface HttpServerConfig extends Config {
    @DefaultValue("8080")
    int port();
    @DefaultValue("10")
    int maxThreads();
}
