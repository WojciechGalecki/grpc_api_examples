package wg.grpc.calculator.client;

import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SquareRootResponse;

public class CalculatorUnaryClient {
    private static final Logger log = Logger.getLogger(CalculatorUnaryClient.class.getName());

    public static void main(String[] args) {
        getSquareRoot();
    }

    private static void getSquareRoot() {
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

            log.info("Square root: " + response.getNumberRoot());
        } catch (StatusRuntimeException e) {
            log.warning("ERROR: ");
            e.printStackTrace();
        }

        channel.shutdown();
    }
}
