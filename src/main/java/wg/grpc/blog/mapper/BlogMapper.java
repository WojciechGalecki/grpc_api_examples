package wg.grpc.blog.mapper;

import org.bson.Document;

import com.proto.blog.Blog;

public class BlogMapper {
    private static final String BLOG_ID = "_id";
    private static final String AUTHOR_ID = "author_id";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    public static Document mapFromBlog(Blog blog) {
        return new Document(AUTHOR_ID, blog.getAuthorId())
            .append(TITLE, blog.getTitle())
            .append(CONTENT, blog.getContent());
    }

    public static Blog mapFromDocument(Document blogDocument) {
        return Blog.newBuilder()
            .setId(blogDocument.getObjectId(BLOG_ID).toString())
            .setAuthorId(blogDocument.getString(AUTHOR_ID))
            .setTitle(blogDocument.getString(TITLE))
            .setContent(blogDocument.getString(CONTENT))
            .build();
    }
}
