package javaFinalProject.controllers;

import java.io.Console;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javaFinalProject.service.RedditService;

@RestController
public class RedditController {

    @Autowired
    public RedditService redditService;

    @GetMapping("/saved")
    public String getSavedPosts() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String authToken = redditService.getAuthToken();
        headers.setBearerAuth(authToken);
        headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String subReddit = "java";
        String url = "https://oauth.reddit.com/r/"+subReddit+"/hot";
        ResponseEntity<String> response
                = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
