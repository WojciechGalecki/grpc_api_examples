package wg.grpc.blog.server;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class BlogServer {
    private static final int PORT = 50051;
    private static final Logger log = Logger.getLogger(BlogServer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        runServer();
    }

    private static void runServer() throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(BlogServer.PORT)
            .addService(new BlogService())
            .build();

        server.start();

        log.info("Server is running...");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
