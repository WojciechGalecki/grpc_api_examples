package wg.grpc.blog.server;

import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import wg.grpc.blog.database.BlogRepository;
import wg.grpc.blog.database.MongodbBlogRepository;
import wg.grpc.blog.mapper.BlogMapper;

import org.bson.Document;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;

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
}
