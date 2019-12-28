package wg.grpc.blog.mapper;

import org.bson.Document;

import com.proto.blog.Blog;

public class BlogMapper {
    private static final String AUTHOR_ID = "author_id";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    public static Document mapFromBlog(Blog blog) {
        return new Document(AUTHOR_ID, blog.getAuthorId())
            .append(TITLE, blog.getTitle())
            .append(CONTENT, blog.getContent());
    }
}
