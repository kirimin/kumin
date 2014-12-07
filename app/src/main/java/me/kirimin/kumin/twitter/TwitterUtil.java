package me.kirimin.kumin.twitter;

import java.util.List;

import me.kirimin.kumin.db.User;

public class TwitterUtil {

    public static User searchNextUser(List<User> userList, String nowAccount) {
        for (int i = 0; i < userList.size(); i++) {
            if (!userList.get(i).getSName().equals(nowAccount)) {
                continue;
            }
            if (userList.size() == i + 1) {
                return userList.get(0);
            } else {
                return userList.get(i + 1);
            }
        }
        return null;
    }

    private TwitterUtil() {
    }
}
