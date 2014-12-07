package me.kirimin.kumin.twitter;

import java.util.List;

import me.kirimin.kumin.db.User;

public class TwitterUtil {

    /**
     * 現在のアカウントから切り替えるべき次のアカウントを探す
     *
     * @param accountList アカウントの一覧
     * @param currentAccount  現在選択中のアカウント
     * @return 受け取ったリストから現アカウントの次の要素のアカウントを返す。見つからなければnullを返す。
     */
    public static User searchNextAccount(List<User> accountList, String currentAccount) {
        for (int i = 0; i < accountList.size(); i++) {
            if (!accountList.get(i).getSName().equals(currentAccount)) {
                continue;
            }
            return i + 1 < accountList.size() ? accountList.get(i + 1) : accountList.get(0);
        }
        return null;
    }

    /**
     * 現在のハッシュタグから切り替えるべき次のハッシュタグを探す
     *
     * @param hashTagList    ハッシュタグの一覧
     * @param currentHashTag 現在選択中のハッシュタグ
     * @return 受け取ったリストから現ハッシュタグの次の要素のハッシュタグを返す。見つからなければnullを返す。
     */
    public static String searchNextHashTag(List<String> hashTagList, String currentHashTag) {
        for (int i = 0; i < hashTagList.size(); i++) {
            if (!hashTagList.get(i).equals(currentHashTag)) {
                continue;
            }
            return i + 1 < hashTagList.size() ? hashTagList.get(i + 1) : hashTagList.get(0);
        }
        return null;
    }

    private TwitterUtil() {
    }
}
