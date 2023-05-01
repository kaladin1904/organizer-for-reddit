package javaFinalProject.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javaFinalProject.models.RedditChildrenObject;
import javaFinalProject.models.RedditDataObject;
import javaFinalProject.models.RedditSavedApiResponse;
import javaFinalProject.models.SavedPost;
import javaFinalProject.service.RedditService;
import javaFinalProject.util.RedditProperties;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RedditController {

    private static String REDIRECT_URI = "http://localhost:4200/test/";
    private static ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private String username;
    // TODO - have to handle refreshing and validity of token - not thought about that yet
    private String authToken;

    @Autowired
    public RedditService redditService;

    @Autowired
    private RedditProperties redditProperties;

    @GetMapping("/getAccessToken")
    public ResponseEntity<Boolean> getAccessToken(
        @RequestParam(required = true, value = "state") String state, @RequestParam(required = true, value = "code") String code) {
        System.out.println(code);
        System.out.println(state);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(redditProperties.getClientID(), redditProperties.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));

        MultiValueMap<String, String> body1 = new LinkedMultiValueMap<String, String>();     
        body1.add("grant_type", "authorization_code");
        body1.add("code", code);
        body1.add("redirect_uri", REDIRECT_URI);


        HttpEntity<?> request = new HttpEntity<Object>(body1, headers);
        String authUrl = "https://www.reddit.com/api/v1/access_token";
        ResponseEntity<String> response = restTemplate.postForEntity(
            authUrl, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        try {
            map.putAll(mapper.readValue(response.getBody(), new TypeReference<Map<String,Object>>(){}));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Response ============" + response.getBody());
        if(String.valueOf(map.get("access_token")) != null) {
            authToken = String.valueOf(map.get("access_token"));
            username = redditService.getUserName(String.valueOf(map.get("access_token")));
            return ResponseEntity.ok(true);
        } 
        
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/saved")
    public ResponseEntity<SavedPost[]> getSavedPosts() {
        //TODO - add check that username and authToken both exist and are valid
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        
        headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        // TODO - add check for username
        String url = "https://oauth.reddit.com/user/" + username + "/saved";
        ResponseEntity<String> response
                = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        if(response.getStatusCode().equals(HttpStatusCode.valueOf(403))) {
            System.err.println(response);  
        }
        System.out.println(response.getStatusCode());
        System.out.println("RESPONSE BODY  =  = = = = = = = = = = " + response.getBody());

        System.out.println("*******************************");
        parseResponseFromRedditSavedApi(response.getBody());
        return null;
    }

    //TODO - move to Service later
    private SavedPost[] parseResponseFromRedditSavedApi(String responseBody) {
          try {
            RedditSavedApiResponse redditSavedApiResponse = mapper.readValue(responseBody, RedditSavedApiResponse.class);
            RedditDataObject redditDataObject = mapper.readValue(
                redditSavedApiResponse.getData().toString(), RedditDataObject.class);
            // System.out.println(redditDataObject.toString());
            List<RedditChildrenObject> childrenObjectsForAllSavedPosts = 
                Arrays.asList(mapper.readValue(redditDataObject.getChildren().toString(), RedditChildrenObject[].class));
            List<SavedPost> allSavedPosts = new ArrayList<>();
            for(RedditChildrenObject redditChildrenObject : childrenObjectsForAllSavedPosts) {
                allSavedPosts.add(mapper.readValue(redditChildrenObject.getData().toString(), SavedPost.class));
            }
            // List<SavedPost> allSavedPosts = 
            //     Arrays.asList(mapper.readValue(redditDataObject.getChildren().toString(), SavedPost[].class));
            for(SavedPost savedPost : allSavedPosts) {
                System.out.println(savedPost);
            }

            
          } catch (JsonProcessingException e) {
               e.printStackTrace();
          } catch (IOException e) {
               e.printStackTrace();
          }

          return null;
    }
}
