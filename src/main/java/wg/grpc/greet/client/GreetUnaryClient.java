package wg.grpc.greet.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;

public class GreetUnaryClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetSyncClient = GreetServiceGrpc.newBlockingStub(channel);

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

        channel.shutdown();
    }
}
