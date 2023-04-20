package javaFinalProject.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javaFinalProject.service.RedditService;
import javaFinalProject.util.RedditProperties;

@Service
public class RedditServiceImpl implements RedditService {

     @Autowired
     private RedditProperties redditProperties;

     public RedditServiceImpl() {}

     public String getAuthToken(){
     RestTemplate restTemplate = new RestTemplate();
     HttpHeaders headers = new HttpHeaders();
     headers.setBasicAuth(redditProperties.getClientID(), redditProperties.getClientSecret());
     headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
     headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));
     String body = "grant_type=client_credentials";
     HttpEntity<String> request
               = new HttpEntity<>(body, headers);
     String authUrl = "https://www.reddit.com/api/v1/access_token";
     ResponseEntity<String> response = restTemplate.postForEntity(
               authUrl, request, String.class);
     ObjectMapper mapper = new ObjectMapper();
     Map<String, Object> map = new HashMap<>();
     try {
          map.putAll(mapper
                    .readValue(response.getBody(), new TypeReference<Map<String,Object>>(){}));
     } catch (IOException e) {
          e.printStackTrace();
     }
     System.out.println(response.getBody());
     return String.valueOf(map.get("access_token"));
     }
   
}
