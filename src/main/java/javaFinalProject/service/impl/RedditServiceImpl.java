package javaFinalProject.service.impl;

import java.io.IOException;
import java.util.Collections;

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
import javaFinalProject.models.User;
import javaFinalProject.service.RedditService;
import javaFinalProject.util.RedditProperties;

@Service
public class RedditServiceImpl implements RedditService {

     @Autowired
     private RedditProperties redditProperties;

     public RedditServiceImpl() {}

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
          // System.out.println(response.getBody());
          ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          try {
               User user = mapper.readValue(response.getBody(), User.class);
               System.out.println(user.name);
               return user.name;
          } catch (JsonProcessingException e) {
               e.printStackTrace();
          } catch (IOException e) {
               e.printStackTrace();
          }
          
          return null;
     }
   
}
