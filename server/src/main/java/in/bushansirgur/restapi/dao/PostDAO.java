package in.bushansirgur.restapi.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import in.bushansirgur.restapi.model.PostModel;

@Repository
public interface PostDAO extends MongoRepository<PostModel, Long> {

    // Custom method to find a user by name
    PostModel findByName(String name);

}
