package wg.grpc.greet.server;

import java.io.File;
import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GreetSecureServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50051)
            .addService(new GreetService())
            .useTransportSecurity(
                new File("ssl/server.crt"),
                new File("ssl/server.pem")
            )
            .build();

        server.start();

        System.out.println("Secure server is running...");

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }
}
