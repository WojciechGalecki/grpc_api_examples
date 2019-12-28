package wg.grpc.blog.client;

import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import com.proto.blog.DeleteBlogRequest;
import com.proto.blog.DeleteBlogResponse;
import com.proto.blog.ReadBlogRequest;
import com.proto.blog.ReadBlogResponse;
import com.proto.blog.UpdateBlogRequest;
import com.proto.blog.UpdateBlogResponse;

public class BlogClient implements GrpcClient {
    private static final Logger log = Logger.getLogger(BlogClient.class.getName());
    private final ManagedChannel CHANNEL = GrpcClient.createChannel(50051);

    private BlogServiceGrpc.BlogServiceBlockingStub blogClient;

    public BlogClient() {
        this.blogClient = BlogServiceGrpc.newBlockingStub(CHANNEL);
    }

    public static void main(String[] args) {
       BlogClient blogClient = new BlogClient();

       try {
           blogClient.run();
       } catch (StatusRuntimeException e) {
           log.warning("Got error from server: " + e.getMessage());
       }
    }

    @Override
    public void run() throws StatusRuntimeException {
        // create
        Blog blogToCreate = Blog.newBuilder()
            .setAuthorId("John")
            .setTitle("New Blog")
            .setContent("Hello world")
            .build();

        String existingBlogId = createBlog(blogToCreate);

        // read
        readBlog(existingBlogId);
        //readBlog("unknown");

        // update
        Blog validBlogToUpdate = Blog.newBuilder()
            .setId(existingBlogId)
            .setAuthorId("John Update")
            .setTitle("New Blog updated")
            .setContent("Hello world updated")
            .build();

        updateBlog(validBlogToUpdate);

        //delete
        deleteBlog(existingBlogId);

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

    private void readBlog(String blogId) {
        ReadBlogRequest request = ReadBlogRequest.newBuilder()
            .setBlogId(blogId)
            .build();

        ReadBlogResponse response = blogClient.readBlog(request);
        log.info("Read blog response: " + response.toString());
    }

    private void updateBlog(Blog blog) {
        UpdateBlogRequest request = UpdateBlogRequest.newBuilder()
            .setBlog(blog)
            .build();

        UpdateBlogResponse response = blogClient.updateBlog(request);
        log.info("Update blog response: " + response.toString());
    }

    private void deleteBlog(String blogId) {
        DeleteBlogRequest request = DeleteBlogRequest.newBuilder()
            .setBlogId(blogId)
            .build();

        DeleteBlogResponse response = blogClient.deleteBlog(request);
        log.info("Deleted blog response: " + response.toString());
    }
}
