package wg.grpc.movie.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class MovieSecureServer {
    private static final Logger LOGGER = Logger.getLogger(MovieServer.class.getName());
    private static final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
            .addService(new MovieService())
            .addService(ProtoReflectionService.newInstance()) // for reflection
            .useTransportSecurity(
                new File("ssl/server.crt"),
                new File("ssl/server.pem")
            )
            .build();

        server.start();
        LOGGER.info("Secure server is running on port: " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
