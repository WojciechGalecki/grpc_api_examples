package wg.grpc.calculator.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50052)
            .addService(new CalculatorService())
            .build();

        server.start();

        System.out.println("Server is running...");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
