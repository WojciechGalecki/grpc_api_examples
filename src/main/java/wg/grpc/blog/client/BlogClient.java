package wg.grpc.blog.client;

import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import wg.grpc.blog.server.IGrpcClient;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;

public class BlogClient implements IGrpcClient {
    private static final Logger log = Logger.getLogger(BlogClient.class.getName());
    private final ManagedChannel CHANNEL = IGrpcClient.createChannel(50051);

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

        createBlog(blogToCreate);

        CHANNEL.shutdown();
    }

    private void createBlog(Blog blog) {
        CreateBlogRequest request = CreateBlogRequest.newBuilder()
            .setBlog(blog)
            .build();

        CreateBlogResponse response = blogClient.createBlog(request);
        log.info("Create blog response: " + response.toString());
    }
}
