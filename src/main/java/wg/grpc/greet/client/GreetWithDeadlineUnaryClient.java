package wg.grpc.greet.client;

import java.util.concurrent.TimeUnit;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadlineRequest;
import com.proto.greet.GreetWithDeadlineResponse;
import com.proto.greet.Greeting;

public class GreetWithDeadlineUnaryClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetSyncClient = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
            .setFirstName("John")
            .build();

        // first deadline - 1000 ms
        try {
            GreetWithDeadlineResponse response = greetSyncClient
                .withDeadline(Deadline.after(1000, TimeUnit.MILLISECONDS))
                .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(greeting)
                    .build());

            System.out.println(response.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded!");
            } else {
                e.printStackTrace();
            }
        }

        // second deadline - 100 ms - should got DEADLINE_EXCEEDED exception from the server
        try {
            GreetWithDeadlineResponse response = greetSyncClient
                .withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(greeting)
                    .build());

            System.out.println(response.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded!");
            } else {
                e.printStackTrace();
            }
        }

        channel.shutdown();
    }
}
