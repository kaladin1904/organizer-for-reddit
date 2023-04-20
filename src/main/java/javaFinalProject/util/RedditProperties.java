package javaFinalProject.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "reddit")
@Component
public class RedditProperties {

    private String clientID;
    private String clientSecret;
    private String accessTokenUri;
    private String userAuthorizationUri;
    private String redirectUri;

    public String getClientID() {
        return clientID;
    }

    public void setClientID(final String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(final String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public void setUserAuthorizationUri(final String userAuthorizationUri) {
        this.userAuthorizationUri = userAuthorizationUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(final String redirectUri) {
        this.redirectUri = redirectUri;
    }

}
