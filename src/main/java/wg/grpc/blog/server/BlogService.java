package wg.grpc.blog.server;

import java.util.logging.Logger;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import wg.grpc.blog.database.BlogRepository;
import wg.grpc.blog.database.MongodbBlogRepository;
import wg.grpc.blog.mapper.BlogMapper;
import wg.grpc.blog.validator.BlogValidator;
import wg.grpc.blog.validator.MongodbBlogValidator;

import org.bson.Document;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.DeleteBlogRequest;
import com.proto.blog.DeleteBlogResponse;
import com.proto.blog.ListBlogRequest;
import com.proto.blog.ListBlogResponse;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;
import com.proto.blog.UpdateBlogRequest;
import com.proto.blog.UpdateBlogResponse;

public class BlogService extends BlogServiceGrpc.BlogServiceImplBase {
    private static final Logger log = Logger.getLogger(BlogService.class.getName());
    private BlogRepository blogRepository = new MongodbBlogRepository();
    private BlogValidator blogValidator = new MongodbBlogValidator();

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        log.info("Received create blog request");
        Blog blog = request.getBlog();

        Document blogDocument = BlogMapper.mapFromBlogWithoutId(blog);

        log.info("Creating new blog...");
        String blogId = blogRepository.createBlog(blogDocument);
        log.info("Successfully created new blog with id: " + blogId);

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
            .setBlog(blog.toBuilder().setId(blogId).build())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        String blogId = request.getBlogId();
        log.info("Received read blog request for id: " + blogId);

        if (validateBlogId(responseObserver, blogId)) {
            log.info("Finding blog...");
            Document blogDocument = blogRepository.findBlog(request.getBlogId());

            if (blogDocument != null) {
                log.info("Successfully found blog");
                Blog blog = BlogMapper.mapFromDocument(blogDocument);

                ReadBlogResponse response = ReadBlogResponse.newBuilder()
                    .setBlog(blog)
                    .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                createErrorResponse(responseObserver, Status.NOT_FOUND,
                    String.format("The blog with id: %s was not found", blogId));
            }
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        Blog blogToUpdate = request.getBlog();
        log.info("Received update blog request");

        String blogId = blogToUpdate.getId();
        if (validateBlogId(responseObserver, blogId)) {
            Document blogDocument = BlogMapper.mapFromBlog(blogToUpdate);

            log.info("Updating blog...");

            if (blogRepository.updateBlog(blogDocument)) {
                log.info("Successfully updated blog");
                UpdateBlogResponse response = UpdateBlogResponse.newBuilder()
                    .setBlog(blogToUpdate)
                    .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                createErrorResponse(responseObserver, Status.NOT_FOUND,
                    String.format("The blog with id: %s was not found", blogId));
            }
        }
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
        String blogId = request.getBlogId();
        log.info("Received delete blog request");

        if (validateBlogId(responseObserver, blogId)) {
            log.info("Deleting blog...");

            if (blogRepository.deleteBlog(blogId)) {
                log.info("Successfully deleted blog");
                DeleteBlogResponse response = DeleteBlogResponse.newBuilder()
                    .setBlogId(blogId)
                    .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                createErrorResponse(responseObserver, Status.NOT_FOUND,
                    String.format("The blog with id: %s was not found", blogId));
            }
        }
    }

    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
        log.info("Received list blog request");

        blogRepository.findAll().iterator().forEachRemaining(document -> responseObserver.onNext(
            ListBlogResponse.newBuilder()
                .setBlog(BlogMapper.mapFromDocument(document))
                .build()
        ));

        responseObserver.onCompleted();
    }

    private boolean validateBlogId(StreamObserver<?> responseObserver, String blogId) {
        log.info("Validate blog id: " + blogId);
        boolean isValid = true;
        if (!blogValidator.isValidId(blogId)) {
            createErrorResponse(responseObserver, Status.INVALID_ARGUMENT,
                "The blog id is not a valid hex string representation");
            isValid = false;
        }

        return isValid;
    }

    private void createErrorResponse(StreamObserver<?> responseObserver, Status status, String msg) {
        responseObserver.onError(
            status
                .withDescription(msg)
                .asRuntimeException()
        );
    }
}