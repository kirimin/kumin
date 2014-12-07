package me.kirimin.kumin.db;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Userのリストから対応したスクリーンネームの配列を取得する
     *
     * @param users Userのリスト
     * @return Userのリストに対応したスクリーンネームの配列
     */
    public static List<String> getUserNames(List<User> users) {
        List<String> userNames = new ArrayList<String>();
        for (User user : users) {
            userNames.add(user.getSName());
        }

        return userNames;
    }
}
