package javaFinalProject.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class SavedPost {

    @JsonProperty("name")
    private String user;

    @JsonProperty("author")
    private String author;

    @JsonProperty("ups")
    private String upvotes;

    @JsonProperty("total_awards_received")
    private String awards;

    @JsonProperty("subreddit_name_prefixed")
    private String subredditName;

    @JsonProperty("title")
    private String  title;

    @JsonProperty("thumbnail")
    private String thumbnailLink;

    @JsonProperty("subreddit_type")
    private String subredditType;

    @JsonProperty("permalink")
    private String link;

    @JsonProperty("num_comments")
    private int num_comments;

}
