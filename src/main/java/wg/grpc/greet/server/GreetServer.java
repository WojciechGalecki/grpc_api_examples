package wg.grpc.greet.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GreetServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50051)
            .addService(new GreetService())
            .build();

        server.start();

        System.out.println("Server is running...");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
