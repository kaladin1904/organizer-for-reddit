package javaFinalProject.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javaFinalProject.models.ActiveUser;
import javaFinalProject.models.SavedPostsList;
import javaFinalProject.models.SavedUser;
import javaFinalProject.models.UserAuthDetails;
import javaFinalProject.models.UserDTO;
import javaFinalProject.models.UsernameDTO;
import javaFinalProject.service.RedditService;
import javaFinalProject.util.RedditProperties;

@CrossOrigin(origins = "https://kaladin1904.github.io")
@RestController
public class RedditController {

    private static String REDIRECT_URI = "https://kaladin1904.github.io/organizer-for-reddit-frontend/saved/";

    @Autowired
    public RedditService redditService;

    @Autowired
    private RedditProperties redditProperties;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        SavedUser retrievedUser = redditService.checkIfUserExists(userDTO.getUsername());
        if (retrievedUser != null){
            UsernameDTO usernameDTO = new UsernameDTO(retrievedUser.getUsername());
            if (retrievedUser.getPassword().equals(userDTO.getPassword())) {
                redditService.addActiveUser(userDTO.getUsername());
                return ResponseEntity.ok(usernameDTO);
            } else {
                return ResponseEntity.badRequest().body("incorrect password");
            }
        }
        return ResponseEntity.badRequest().body("user does not exist");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        SavedUser retrievedUser = redditService.checkIfUserExists(userDTO.getUsername());
        if(retrievedUser != null) {
            return ResponseEntity.badRequest().body("user already exists");
        }
        SavedUser user =  redditService.createUser(userDTO);
        UsernameDTO usernameDTO = new UsernameDTO(user.getUsername());
        redditService.addActiveUser(userDTO.getUsername());
        return ResponseEntity.ok().body(usernameDTO);
    }

    private ResponseEntity<?> existingUser(String state, String code, String username){
        SavedUser savedUser = redditService.checkIfUserExists(username);
        UserAuthDetails savedUserAuthDetails = redditService.getUserAuthDetails(username);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(redditProperties.getClientID(), redditProperties.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", savedUserAuthDetails.getRefreshToken());

        HttpEntity<?> request = new HttpEntity<Object>(body, headers);
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

        if(String.valueOf(map.get("access_token")) != null) {
            boolean flag = redditService.saveUserAuthDetails(
                username,
                String.valueOf(map.get("access_token")),
                String.valueOf(map.get("refresh_token")));
            UsernameDTO usernameDTO = new UsernameDTO(savedUser.getUsername());
            return flag ? ResponseEntity.ok(usernameDTO) : ResponseEntity.notFound().build();
        } 
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> firstTimeUser(String state, String code, String username) {
        SavedUser savedUser = redditService.checkIfUserExists(username);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(redditProperties.getClientID(), redditProperties.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", REDIRECT_URI);


        HttpEntity<?> request = new HttpEntity<Object>(body, headers);
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
        if(String.valueOf(map.get("access_token")) != null) {
            boolean flag = redditService.saveUserAuthDetails(
                username,
                String.valueOf(map.get("access_token")),
                String.valueOf(map.get("refresh_token")));
            UsernameDTO usernameDTO = new UsernameDTO(savedUser.getUsername());
            return flag ? ResponseEntity.ok(usernameDTO) : ResponseEntity.notFound().build();
        } 
        return ResponseEntity.notFound().build();
    
        
    }

    @GetMapping("/getAccessToken")
    public ResponseEntity<?> getAccessToken(
        @RequestParam(required = true, value = "state") String state,
        @RequestParam(required = true, value = "code") String code,
        @RequestParam(required = false, value = "username") String username) {
        SavedUser savedUser = redditService.checkIfUserExists(username);
        ActiveUser activeUser = redditService.isLoggedIn(username);
        UserAuthDetails authDetails = redditService.getUserAuthDetails(username);
        if(code.equals("existingUser") || (activeUser!=null && savedUser!=null && authDetails != null)) {
            return existingUser(code, state, username);
        }else {
            return firstTimeUser(state, code, username);
        }
    }
    
    @GetMapping("/saved")
    public ResponseEntity<SavedPostsList> getSavedPosts(
        @RequestParam(required = true, value = "username") String username
    ) {
        UserAuthDetails savedUserAuthDetails = redditService.getUserAuthDetails(username);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(savedUserAuthDetails.getAccessToken());
        
        headers.put("User-Agent", Collections.singletonList("(by /u/kaladin1904)"));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String redditUsername = redditService.getUserName(savedUserAuthDetails.getAccessToken());
        String url = "https://oauth.reddit.com/user/" + redditUsername + "/saved?limit=100";
        ResponseEntity<String> response
                = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        if(response.getStatusCode().equals(HttpStatusCode.valueOf(403))) {
            System.err.println(response);  
        }

        SavedPostsList allSavedPostsList = redditService.parseResponseFromRedditSavedApi(response.getBody());
        return ResponseEntity.ok(allSavedPostsList);
    }

    @GetMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(
        @RequestParam(required = true, value = "username") String username) {
        
        SavedUser savedUser = redditService.checkIfUserExists(username);
        ActiveUser activeUser = redditService.isLoggedIn(username);
        if(savedUser != null && activeUser != null) {
            UsernameDTO usernameDTO = new UsernameDTO(username);
            return ResponseEntity.ok(usernameDTO);
        }else {
            return ResponseEntity.badRequest().body("the user does not exist or is not active");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout( @RequestParam(required = true, value = "username") String username) {
        SavedUser savedUser = redditService.checkIfUserExists(username);
        ActiveUser activeUser = redditService.isLoggedIn(username);
        if(savedUser != null && activeUser != null) {
            redditService.removeActiveUser(activeUser);
            UsernameDTO usernameDTO = new UsernameDTO(username);
            return ResponseEntity.ok(usernameDTO);
        }  else {
            return ResponseEntity.badRequest().body("the user does not exist or is not active");
        }
    }
}
