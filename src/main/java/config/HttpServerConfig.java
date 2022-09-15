package config;

import org.aeonbits.owner.Config;

@Config.Sources({"system:env", "classpath:config/httpserver.properties"})
public interface HttpServerConfig extends Config {
    @DefaultValue("8080")
    int port();
    @DefaultValue("2")
    int maxThreads();

    @DefaultValue("./src/html/index.html")
    String homePage();

    @DefaultValue(".")
    String webPath();

    @DefaultValue("test")
    String serverName();

    @DefaultValue("1")
    boolean cgiEnable();

    @DefaultValue("./src/cgi_bin")
    String cgiPath();

    @DefaultValue("./src/html")
    String htmlPath();

    @DefaultValue("./src/html/404.html")
    String notFoundPath();

    @DefaultValue("5000")
    int maxCGITime();
}
