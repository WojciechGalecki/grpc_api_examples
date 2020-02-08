package wg.grpc.movie.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.proto.movie.AddMovieResponse;
import com.proto.movie.AddMultipleMoviesResponse;
import com.proto.movie.Movie;
import com.proto.movie.MovieServiceGrpc;

public class MovieService extends MovieServiceGrpc.MovieServiceImplBase {
    private final Logger LOGGER = Logger.getLogger(MovieService.class.getName());

    @Override
    public void addMovie(Movie request, StreamObserver<AddMovieResponse> responseObserver) {
        try {
            String movieId = someMethodToPerformRequest(request);

            AddMovieResponse response = AddMovieResponse.newBuilder()
                .setMovieId(movieId)
                .build();

            responseObserver.onNext(response);
        } catch (Exception e) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription("???")
                    .withCause(new Throwable("???"))
                    .asRuntimeException()
            );
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Movie> addMultipleMovies(StreamObserver<AddMultipleMoviesResponse> responseObserver) {

        StreamObserver<Movie> requestStreamObserver = new StreamObserver<>() {

            List<String> performedMovieIds = new ArrayList<>();

            @Override
            public void onNext(Movie value) {
                String movieId = someMethodToPerformRequest(value);
                performedMovieIds.add(movieId);
                LOGGER.info("Performing a movie with id: " + movieId);
            }

            @Override
            public void onError(Throwable t) {
                // do something
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AddMultipleMoviesResponse.newBuilder()
                    .addAllMovieIds(performedMovieIds)
                    .build());

                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    private String someMethodToPerformRequest(Movie movie) {
        // logic
        return UUID.randomUUID().toString() + " from Java";
    }
}
