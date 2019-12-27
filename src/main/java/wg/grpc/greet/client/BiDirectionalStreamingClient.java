package wg.grpc.greet.client;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;

public class BiDirectionalStreamingClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();

        // asynchronous
        GreetServiceGrpc.GreetServiceStub greetAsyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestStreamObserver = greetAsyncClient.greetEveryone(new StreamObserver<>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Response from server completed");
                latch.countDown();
            }
        });

        Map.of(
            "Hans", "Gruber",
            "John", "McClane",
            "Holly", "Gennero"
        ).forEach((k, v) -> {
            System.out.println("Sending message: " + k);

            Greeting greeting = Greeting.newBuilder()
                .setFirstName(k)
                .setLastName(v)
                .build();

            requestStreamObserver.onNext(GreetEveryoneRequest.newBuilder()
                .setGreeting(greeting)
                .build());
        });

        // information for server that client has finished sending data
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        channel.shutdown();
    }
}
