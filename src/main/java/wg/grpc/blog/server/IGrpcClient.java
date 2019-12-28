package wg.grpc.blog.server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public interface IGrpcClient {
    static ManagedChannel createChannel(int port) {
        return ManagedChannelBuilder.forAddress("localhost", port)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();
    }

    void run();
}
