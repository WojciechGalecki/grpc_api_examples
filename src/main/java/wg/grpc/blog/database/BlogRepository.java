package wg.grpc.blog.database;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public interface BlogRepository {
    String createBlog(Document blogDocument);
    Document findBlog(String blogId);
    boolean updateBlog(Document blogToUpdate);
    boolean deleteBlog(String blogId);
    FindIterable<Document> findAll();
}
