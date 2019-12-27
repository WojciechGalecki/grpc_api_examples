package wg.grpc.greet.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class GreetServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50051)
            .addService(new GreetService())
            .addService(ProtoReflectionService.newInstance()) // for reflection
            .build();

        server.start();

        System.out.println("Server is running...");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
