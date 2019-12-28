package wg.grpc.blog.database;

import org.bson.Document;

public interface BlogRepository {
    String createBlog(Document blogDocument);
    Document findBlog(String blogId) throws IllegalArgumentException;
}
