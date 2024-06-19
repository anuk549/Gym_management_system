package in.bushansirgur.restapi.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import in.bushansirgur.restapi.dao.PostDAO;
import in.bushansirgur.restapi.model.PostModel;
import in.bushansirgur.restapi.service.SequenceGeneratorService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    @Autowired
    PostDAO postDAO;

    @Autowired
    SequenceGeneratorService seqGeneratorService;



    @PostMapping("/createPost")
    public PostModel createPost(@RequestParam("image") MultipartFile image,
                                @RequestParam("description") String description,
                                @RequestParam("userName") String userName) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be empty");
        }

        // Assuming you're saving the image as a byte array
        byte[] imageData;
        try {
            imageData = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image data", e);
        }

        // Create a new post object
        PostModel newPostObject = new PostModel();
        newPostObject.setId(seqGeneratorService.generateSequence(PostModel.SEQUENCE_NAME));
        newPostObject.setName(userName); // Set the user's name
        newPostObject.setDescription(description);
        newPostObject.setImageData(imageData);

        return postDAO.save(newPostObject);
    }




    @GetMapping("/readPost")
    public List<PostModel> readPosts() {
        return postDAO.findAll();
    }

    @GetMapping("/readPost/{id}")
    public PostModel readPost(@PathVariable Long id) {
        Optional<PostModel> postObj = postDAO.findById(id);
        if (postObj.isPresent()) {
            return postObj.get();
        } else {
            throw new RuntimeException("Post not found with id " + id);
        }
    }




    // PostController.java

    @PutMapping("/updatePost/{id}")
    public ResponseEntity<PostModel> updatePost(@PathVariable Long id,
                                                @RequestParam("description") String description,
                                                @RequestParam(value = "image", required = false) MultipartFile image) {
        Optional<PostModel> existingPostOptional = postDAO.findById(id);
        if (existingPostOptional.isPresent()) {
            PostModel existingPost = existingPostOptional.get();
            existingPost.setDescription(description);
            if (image != null) {
                try {
                    existingPost.setImageData(image.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read image data", e);
                }
            }
            PostModel savedPost = postDAO.save(existingPost);
            return ResponseEntity.ok(savedPost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {
        Optional<PostModel> postObj = postDAO.findById(id);
        if (postObj.isPresent()) {
            postDAO.delete(postObj.get());
            return "Post deleted with id " + id;
        } else {
            throw new RuntimeException("Post not found for id " + id);
        }
    }
}
