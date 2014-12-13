package me.kirimin.kumin.model;

public class User {

    private String id;
    private String sName;
    private String token;
    private String secret;

    public User(String id, String screenName, String token, String secret) {
        this.id = id;
        this.sName = screenName;
        this.token = token;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public String getSName() {
        return sName;
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }
}
