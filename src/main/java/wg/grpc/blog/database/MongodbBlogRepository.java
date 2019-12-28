package wg.grpc.blog.database;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongodbBlogRepository implements BlogRepository {
    private static final String MONGO_DB_URL = "mongodb://user:user@localhost:27017";
    private static final String BLOG_DB = "blog";
    private static final String BLOG_TABLE = "blog";
    private static final String BLOG_ID = "_id";

    private final MongoClient mongoClient = MongoClients.create(MONGO_DB_URL);
    private final MongoDatabase blogDb = mongoClient.getDatabase(BLOG_DB);
    private final MongoCollection<Document> blogTable = blogDb.getCollection(BLOG_TABLE);

    public MongodbBlogRepository() {
    }

    @Override
    public String createBlog(Document blogDocument) {
        blogTable.insertOne(blogDocument);

        return blogDocument.getObjectId(BLOG_ID).toString();
    }

    @Override
    public Document findBlog(String blogId) throws IllegalArgumentException {
        return blogTable.find(eq(BLOG_ID, new ObjectId(blogId)))
            .first();
    }
}
