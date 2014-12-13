package me.kirimin.kumin.model;

import java.util.Date;

public class Tweet {

    private long userId;
    private String text;
    private String name;
    private String screenName;
    private String iconUrl;
    private Date createdAt;

    public Tweet(long userId, String text, String name, String screenName, String iconUrl, Date createdAt) {
        this.userId = userId;
        this.text = text;
        this.name = name;
        this.screenName = screenName;
        this.iconUrl = iconUrl;
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}