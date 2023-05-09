package javaFinalProject.service;

import javaFinalProject.models.SavedPostsList;
import javaFinalProject.models.SavedUser;
import javaFinalProject.models.UserAuthDetails;
import javaFinalProject.models.UserDTO;

public interface RedditService {

    public String getUserName(String token);

    public SavedPostsList parseResponseFromRedditSavedApi(String responseBody);

    public SavedUser checkIfUserExists(String username);

    public SavedUser createUser(UserDTO userDTO);

    public boolean saveUserAuthDetails(String username, String accessToken, String refreshToken);

    public UserAuthDetails getUserAuthDetails(String username);

    
}
