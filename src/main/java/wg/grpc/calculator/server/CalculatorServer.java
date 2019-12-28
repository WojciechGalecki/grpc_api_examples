package wg.grpc.calculator.server;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CalculatorServer {
    private static final Logger log = Logger.getLogger(CalculatorServer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        runServer();
    }

    private static void runServer() throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(50052)
            .addService(new CalculatorService())
            .build();

        server.start();

        log.info("Server is running...");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
