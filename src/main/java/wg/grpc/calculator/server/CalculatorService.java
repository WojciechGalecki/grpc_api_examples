package wg.grpc.calculator.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SquareRootRequest;
import com.proto.calculator.SquareRootResponse;

public class CalculatorService extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        int number = request.getNumber();

        if (number >= 0) {
            double sqrt = Math.sqrt(number);

            responseObserver.onNext(SquareRootResponse.newBuilder()
                .setNumberRoot(sqrt)
                .build());

            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(number + " is not positive number!")
                    .asRuntimeException()
            );
        }
    }
}
