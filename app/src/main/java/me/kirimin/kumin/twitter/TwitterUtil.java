package me.kirimin.kumin.twitter;

import java.util.List;

import me.kirimin.kumin.db.User;

public class TwitterUtil {

    /**
     * 現在のアカウントから切り替えるべき次のアカウントを探す
     *
     * @param accountList アカウントの一覧
     * @param nowAccount  現在選択中のアカウント
     * @return 受け取ったリストから現アカウントの次の要素のアカウントを返す。見つからなければnullを返す。
     */
    public static User searchNextUser(List<User> accountList, String nowAccount) {
        for (int i = 0; i < accountList.size(); i++) {
            if (!accountList.get(i).getSName().equals(nowAccount)) {
                continue;
            }
            if (accountList.size() == i + 1) {
                return accountList.get(0);
            } else {
                return accountList.get(i + 1);
            }
        }
        return null;
    }

    private TwitterUtil() {
    }
}
