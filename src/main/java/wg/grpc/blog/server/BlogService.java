package wg.grpc.blog.server;

import java.util.logging.Logger;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import wg.grpc.blog.database.BlogRepository;
import wg.grpc.blog.database.MongodbBlogRepository;
import wg.grpc.blog.mapper.BlogMapper;

import org.bson.Document;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;

public class BlogService extends BlogServiceGrpc.BlogServiceImplBase {
    private static final Logger log = Logger.getLogger(BlogService.class.getName());
    private BlogRepository blogRepository = new MongodbBlogRepository();

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        log.info("Received create blog request");
        Blog blog = request.getBlog();

        Document blogDocument = BlogMapper.mapFromBlog(blog);

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

        log.info("Finding blog...");
        Document blogDocument = null;

        try {
            blogDocument = blogRepository.findBlog(request.getBlogId());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(String.format("The blog with id: %s was not found", blogId))
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException()
            );
        }

        if (blogDocument != null) {
            log.info("Successfully found blog");
            Blog blog = BlogMapper.mapFromDocument(blogDocument);

            ReadBlogResponse response = ReadBlogResponse.newBuilder()
                .setBlog(blog)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
