package wg.grpc.movie.client;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;

import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import com.proto.movie.AddMovieResponse;
import com.proto.movie.Movie;
import com.proto.movie.MovieServiceGrpc;

public class MovieUnaryClient {
    private static final Logger LOGGER = Logger.getLogger(MovieUnaryClient.class.getName());
    private static final int PORT = 50051;

    public static void main(String[] args) throws SSLException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", PORT)
            .usePlaintext() // disable SSL - for local development
            .build();

        // !!! use this channel for secure connection !!!
        ManagedChannel secureChannel = NettyChannelBuilder.forAddress("localhost", PORT)
            .sslContext(GrpcSslContexts
                .forClient()
                .trustManager(new File("ssl/ca.crt"))
                .build())
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
