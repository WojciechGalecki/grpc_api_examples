package wg.grpc.greet.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;

public class GreetClientStreamingClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();

        // asynchronous
        GreetServiceGrpc.GreetServiceStub greetAsyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestStreamObserver = greetAsyncClient.longGreet(new StreamObserver<>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // onNext will be called only once - client streaming -> one server response
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

        Greeting greeting1 = Greeting.newBuilder()
            .setFirstName("John")
            .setLastName("Doe")
            .build();

        Greeting greeting2 = Greeting.newBuilder()
            .setFirstName("Frank")
            .setLastName("Underwood")
            .build();

        // streaming message #1
        System.out.println("Sending message 1...");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(greeting1)
            .build());

        // streaming message #2
        System.out.println("Sending message 2...");
        requestStreamObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(greeting2)
            .build());

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
