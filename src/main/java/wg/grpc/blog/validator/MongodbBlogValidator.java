package wg.grpc.blog.validator;

import org.bson.types.ObjectId;

public class MongodbBlogValidator implements BlogValidator {

    @Override
    public boolean isValidId(String blogId) {
        boolean isValid = true;

        try {
            new ObjectId(blogId);
        } catch (IllegalArgumentException e) {
            isValid = false;
        }

        return isValid;
    }
}
