package javaFinalProject.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javaFinalProject.models.RedditChildrenObject;
import javaFinalProject.models.RedditDataObject;
import javaFinalProject.models.RedditSavedApiResponse;
import javaFinalProject.models.SavedPost;
import javaFinalProject.models.SavedPostsList;
import javaFinalProject.models.SavedUser;
import javaFinalProject.models.SavedUserRepository;
import javaFinalProject.models.RedditUsername;
import javaFinalProject.models.UserAuthDetails;
import javaFinalProject.models.UserAuthDetailsRepository;
import javaFinalProject.models.UserDTO;
import javaFinalProject.service.RedditService;

@Service
public class RedditServiceImpl implements RedditService {

     private static ObjectMapper mapper = new ObjectMapper()
     .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

     @Autowired
    private SavedUserRepository savedUserRepository;

    @Autowired
    private UserAuthDetailsRepository userAuthDetailsRepository;

     public RedditServiceImpl() {}


     @Override
     public SavedUser checkIfUserExists(String username) {
          final SavedUser savedUser = savedUserRepository.findByUsername(username);
          return savedUser;
    }

    @Override
    public SavedUser createUser(UserDTO userDTO) {
          SavedUser user = SavedUser.builder().
               username(userDTO.getUsername()).
               userId(UUID.randomUUID()).
               password(userDTO.getPassword()).build();
          savedUserRepository.save(user);
          return user;
    }

    @Override
    public boolean saveUserAuthDetails(String username, String accessToken, String refreshToken) {
          UserAuthDetails details = UserAuthDetails.builder().
               username(username).
               accessToken(accessToken).
               refreshToken(refreshToken).build();
          userAuthDetailsRepository.save(details);
          return true;
    }

    @Override
    public UserAuthDetails getUserAuthDetails(String username) {
     UserAuthDetails details = userAuthDetailsRepository.findByUsername(username);
     return details;
    }

     @Override
     public String getUserName(String token){
          RestTemplate restTemplate = new RestTemplate();
          HttpHeaders headers = new HttpHeaders();
          headers.setBearerAuth(token);
          
          headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));
          HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
          String url = "https://oauth.reddit.com/api/v1/me";
          ResponseEntity<String> response
                    = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
          
          if(response.getStatusCode().equals(HttpStatusCode.valueOf(403))) {
               System.err.println(response);  
          }
          ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          try {
               RedditUsername redditUsername = mapper.readValue(response.getBody(), RedditUsername.class);
               return redditUsername.name;
          } catch (JsonProcessingException e) {
               e.printStackTrace();
          } catch (IOException e) {
               e.printStackTrace();
          } 
          return null;
     }
   
     public SavedPostsList parseResponseFromRedditSavedApi(String responseBody) {
          try {
               RedditSavedApiResponse redditSavedApiResponse = mapper.readValue(responseBody, RedditSavedApiResponse.class);
               RedditDataObject redditDataObject = mapper.readValue(
               redditSavedApiResponse.getData().toString(), RedditDataObject.class);
               List<RedditChildrenObject> childrenObjectsForAllSavedPosts = 
                     Arrays.asList(mapper.readValue(redditDataObject.getChildren().toString(), RedditChildrenObject[].class));
               SavedPostsList allSavedPostsList = new SavedPostsList();
               for(RedditChildrenObject redditChildrenObject : childrenObjectsForAllSavedPosts) {
               allSavedPostsList.getAllSavedPosts()
                    .add(mapper.readValue(redditChildrenObject.getData().toString(), SavedPost.class));
               }
               return allSavedPostsList;
          } catch (JsonProcessingException e) {
               e.printStackTrace();
          } catch (IOException e) {
               e.printStackTrace();
          }

          return null;
    }
}
