package wg.grpc.blog.database;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongodbBlogRepository implements BlogRepository {
    private static final String MONGO_DB_URL = "mongodb://user:user@localhost:27017";
    private static final String BLOG_DB = "blog";
    private static final String BLOG_TABLE = "blog";
    private static final String BLOG_ID = "_id";

    private final MongoClient mongoClient = MongoClients.create(MONGO_DB_URL);
    private final MongoDatabase blogDb = mongoClient.getDatabase(BLOG_DB);
    private final MongoCollection<Document> blogTable = blogDb.getCollection(BLOG_TABLE);

    @Override
    public String createBlog(Document blogDocument) {
        blogTable.insertOne(blogDocument);

        return blogDocument.getObjectId(BLOG_ID).toString();
    }

    @Override
    public Document findBlog(String blogId) {
        return blogTable.find(eq(BLOG_ID, new ObjectId(blogId)))
            .first();
    }

    @Override
    public boolean updateBlog(Document blogToUpdate) {
        UpdateResult updateResult = blogTable.replaceOne(eq(BLOG_ID, blogToUpdate.getObjectId(BLOG_ID)), blogToUpdate);

        return updateResult.getModifiedCount() == 1;
    }

    @Override
    public boolean deleteBlog(String blogId) {
        DeleteResult deleteResult = blogTable.deleteOne(eq(BLOG_ID, new ObjectId(blogId)));

        return deleteResult.getDeletedCount() == 1;
    }

    @Override
    public FindIterable<Document> findAll() {
        return blogTable.find();
    }
}
