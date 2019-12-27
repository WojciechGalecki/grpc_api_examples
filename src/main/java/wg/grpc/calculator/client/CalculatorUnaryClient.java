package wg.grpc.calculator.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SquareRootResponse;

public class CalculatorUnaryClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
            // disable SSL/TLS - for local development
            .usePlaintext()
            .build();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorSyncClient = CalculatorServiceGrpc.newBlockingStub(channel);

        // negative number will throw an error
        int number = -1;

        try {
            SquareRootResponse response = calculatorSyncClient.squareRoot(SquareRootRequest.newBuilder()
                .setNumber(number)
                .build());

            System.out.println("Square root: " + response.getNumberRoot());
        } catch (StatusRuntimeException e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }

        channel.shutdown();
    }
}
