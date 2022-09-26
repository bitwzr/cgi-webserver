# cgi-webserver

### Configuration

We store all the config files in `classpath/config` directory. All 
configuration files are named in lowercase with the class name of the 
corresponding configuration class, and the file extension is `.properties`.

In the config file, all configuration items are specified in `key=value` format.
- example:
    - Config file for class `HttpServerConfig` is `config/httpserver.properties`
        ```
        port=8080
        maxThreads=1024
        ```

You can also specify the configurations through the environmental variables, 
and these items have higher priority than the configuration files.

### File Tree
```bash
cgi-webserver
├── LICENSE
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── config
│   │   │   │   └── HttpServerConfig.java
│   │   │   ├── httpserver
│   │   │   │   ├── HttpServer.java
│   │   │   │   └── WorkerThread.java
│   │   │   └── Main.java
│   │   └── resources
│   └── test
│       └── java
│           └── TestClient.java
└── target
    ├── classes     # classpath
    │   ├── config  # store all config files here
    │   │   ├── HttpServerConfig.class
    │   │   └── httpserver.properties
    │   ├── httpserver
    │   │   ├── HttpServer.class
    │   │   └── WorkerThread.class
    │   └── Main.class
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    └── test-classes
        └── TestClient.class
```


