package javaFinalProject.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SavedPostsList {

    private List<SavedPost> allSavedPosts;

    public SavedPostsList() {
        allSavedPosts = new ArrayList<>();
    }
    
    
}
