package wg.grpc.blog.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public interface GrpcClient {
    static ManagedChannel createChannel(int port) {
        return ManagedChannelBuilder.forAddress("localhost", port)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();
    }

    void run() throws StatusRuntimeException;
}