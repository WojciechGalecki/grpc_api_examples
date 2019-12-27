package wg.grpc.greet.server;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetManyTimesResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadlineRequest;
import com.proto.greet.GreetWithDeadlineResponse;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;

public class GreetService extends GreetServiceGrpc.GreetServiceImplBase {

    // unary API
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // extract fields from request
        Greeting greeting = request.getGreeting();

        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        // create response
        String result = String.format("Hello %s %s from java", firstName, lastName);

        GreetResponse response = GreetResponse.newBuilder()
            .setResult(result)
            .build();

        // send response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();
    }

    // server streaming API
    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        Greeting greeting = request.getGreeting();

        String firstName = greeting.getFirstName();
        String lastName = greeting.getLastName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = String.format("Hello %s %s from java, response no: %d", firstName, lastName, i + 1);

                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();

                responseObserver.onNext(response);

                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    // client streaming API
    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        StreamObserver<LongGreetRequest> requestStreamObserver = new StreamObserver<>() {

            String result = "Hello";

            @Override
            public void onNext(LongGreetRequest value) {
                Greeting greeting = value.getGreeting();
                result += String.format(" %s %s", greeting.getFirstName(), greeting.getLastName());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(LongGreetResponse.newBuilder()
                    .setResult(result + "from java")
                    .build()
                );
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    // bi-directional streaming API
    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestStreamObserver = new StreamObserver<>() {

            @Override
            public void onNext(GreetEveryoneRequest value) {
                Greeting greeting = value.getGreeting();
                String result = String.format("Hello %s %s from java", greeting.getFirstName(), greeting.getLastName());

                responseObserver.onNext(GreetEveryoneResponse.newBuilder()
                    .setResult(result)
                    .build());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        Context current = Context.current();

        try {
            // Simulate server deadline
            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("Sleep for 100ms");
                    Thread.sleep(100);
                } else {
                    return;
                }
            }

            responseObserver.onNext(GreetWithDeadlineResponse.newBuilder()
                .setResult("Hello " + request.getGreeting().getFirstName())
                .build()
            );

            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
