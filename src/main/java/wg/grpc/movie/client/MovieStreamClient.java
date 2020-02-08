package wg.grpc.movie.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import com.proto.movie.AddMultipleMoviesResponse;
import com.proto.movie.Movie;
import com.proto.movie.MovieServiceGrpc;

public class MovieStreamClient {
    private static final Logger LOGGER = Logger.getLogger(MovieStreamClient.class.getName());
    private static final int PORT = 50051;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", PORT)
            .usePlaintext() // disable SSL - for local development
            .build();

        // asynchronous
        MovieServiceGrpc.MovieServiceStub movieAsyncClient = MovieServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Movie> requestStreamObserver = movieAsyncClient
            .addMultipleMovies(new StreamObserver<>() {
                @Override
                public void onNext(AddMultipleMoviesResponse value) {
                    // onNext will be called only once - client streaming -> one server response
                    LOGGER.info("Response: " + value.toString());
                }

                @Override
                public void onError(Throwable t) {
                    // do something
                }

                @Override
                public void onCompleted() {
                    LOGGER.info("Response from server completed");
                    latch.countDown();
                }
            });

        // streaming behaviour
        for (int i = 1; i < 4; i++) {
            Movie movie = Movie.newBuilder()
                .setTitle("Movie " + i)
                .build();

            LOGGER.info("Sending request no " + i);
            requestStreamObserver.onNext(movie);
        }
        // information for server that client has finished sending the data
        requestStreamObserver.onCompleted();

        latch.await(3L, TimeUnit.SECONDS);

        channel.shutdown();
    }
}
