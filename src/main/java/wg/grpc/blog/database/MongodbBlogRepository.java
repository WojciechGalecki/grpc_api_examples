package wg.grpc.blog.database;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongodbBlogRepository implements BlogRepository {
    private static final String MONGO_DB_URL = "mongodb://user:user@localhost:27017";
    private static final String BLOG_DB = "blog";
    private static final String BLOG_TABLE = "blog";
    private static final String BLOG_ID = "_id";

    private MongoClient mongoClient = MongoClients.create(MONGO_DB_URL);
    private MongoDatabase blogDb = mongoClient.getDatabase(BLOG_DB);
    private MongoCollection<Document> blogTable = blogDb.getCollection(BLOG_TABLE);

    public MongodbBlogRepository() {
    }

    @Override
    public String createBlog(Document blogDocument) {
        blogTable.insertOne(blogDocument);

        return blogDocument.getObjectId(BLOG_ID).toString();
    }
}
