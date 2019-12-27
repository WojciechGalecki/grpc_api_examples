package wg.grpc.greet.client;

import java.io.File;

import javax.net.ssl.SSLException;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;

public class GreetSecureUnaryClient {

    public static void main(String[] args) throws SSLException {

        ManagedChannel secureChannel = NettyChannelBuilder.forAddress("localhost", 50051)
            .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
            .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetSyncClient = GreetServiceGrpc.newBlockingStub(secureChannel);

        // create protocol buffer message
        Greeting greeting = Greeting.newBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .build();

        GreetRequest request = GreetRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        GreetResponse greetResponse = greetSyncClient.greet(request);

        System.out.println(greetResponse.getResult());

        secureChannel.shutdown();
    }
}
