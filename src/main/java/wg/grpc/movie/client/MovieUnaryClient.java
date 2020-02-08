package wg.grpc.movie.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import com.proto.movie.AddMovieResponse;
import com.proto.movie.Movie;
import com.proto.movie.MovieServiceGrpc;

public class MovieUnaryClient {
    private static final Logger LOGGER = Logger.getLogger(MovieUnaryClient.class.getName());
    private static final int PORT = 50051;

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", PORT)
            .usePlaintext() // disable SSL - for local development
            .build();

        MovieServiceGrpc.MovieServiceBlockingStub movieSyncClient = MovieServiceGrpc.newBlockingStub(channel);

        Movie movie = Movie.newBuilder()
            .setTitle("Pulp Fiction")
            .build();

        try {
            AddMovieResponse response = movieSyncClient
                .withDeadline(Deadline.after(1, TimeUnit.SECONDS))
                .addMovie(movie);

            LOGGER.info("Response: " + response.toString());
        } catch (StatusRuntimeException e) {
            // do something
        }
        channel.shutdown();
    }
}
