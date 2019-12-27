package wg.grpc.greet.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;

public class GreetServerStreamingClient {

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

        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        greetSyncClient.greetManyTimes(request)
            .forEachRemaining(greetManyTimesResponse -> {
                System.out.println(greetManyTimesResponse.getResult());
            });

        channel.shutdown();
    }
}
