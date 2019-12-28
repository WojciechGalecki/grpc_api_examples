package wg.grpc.blog.client;

import java.util.logging.Logger;

import io.grpc.ManagedChannel;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;

public class BlogClient implements GrpcClient {
    private static final Logger log = Logger.getLogger(BlogClient.class.getName());
    private final ManagedChannel CHANNEL = GrpcClient.createChannel(50051);

    private BlogServiceGrpc.BlogServiceBlockingStub blogClient;

    public BlogClient() {
        this.blogClient = BlogServiceGrpc.newBlockingStub(CHANNEL);
    }

    public static void main(String[] args) {
       BlogClient blogClient = new BlogClient();

       blogClient.run();
    }

    @Override
    public void run() {
        Blog blogToCreate = Blog.newBuilder()
            .setAuthorId("John")
            .setTitle("New Blog")
            .setContent("Hello world")
            .build();

        String existingBlogId = createBlog(blogToCreate);

        readBlog(existingBlogId);
        readBlog("unknown");

        CHANNEL.shutdown();
    }

    private String createBlog(Blog blog) {
        CreateBlogRequest request = CreateBlogRequest.newBuilder()
            .setBlog(blog)
            .build();

        CreateBlogResponse response = blogClient.createBlog(request);
        log.info("Create blog response: " + response.toString());

        return response.getBlog().getId();
    }

    private Blog readBlog(String blogId) {
        ReadBlogRequest request = ReadBlogRequest.newBuilder()
            .setBlogId(blogId)
            .build();

        ReadBlogResponse response = blogClient.readBlog(request);
        log.info("Read blog response: " + response.toString());

        return response.getBlog();
    }
}
