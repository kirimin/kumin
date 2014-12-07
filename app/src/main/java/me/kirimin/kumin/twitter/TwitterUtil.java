package me.kirimin.kumin.twitter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.kirimin.kumin.db.User;

public class TwitterUtil {

    /**
     * 現在のアカウントから切り替えるべき次のアカウントを探す
     *
     * @param accountList    アカウントの一覧
     * @param currentAccount 現在選択中のアカウント
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

    /**
     * ツイート文字列を生成する
     *
     * @param tweetMessage ツイート本文
     * @param hashTag      ハッシュタグ。未設定の場合は#のみが渡される想定
     * @return ハッシュタグが設定されていれば本文+ハッシュタグ、設定されていなければ本文をそのまま返す
     */
    public static String buildTweet(String tweetMessage, String hashTag) {
        return hashTag.length() == 1 ? tweetMessage : tweetMessage + " " + hashTag;
    }

    /**
     * ツイートに表示するタイムスタンプ文字列を生成する
     *
     * @param createdAt ツイート投稿時間
     * @return 現在時刻の差によってXXs, XXm, XXh, mm/ddのいずれかのフォーマットでツイート時刻を返す
     */
    public static String getTimeStamp(Date createdAt) {
        Calendar nowCal = Calendar.getInstance();
        Calendar createdAtCal = Calendar.getInstance();
        createdAtCal.setTime(createdAt);

        if (!isToday(nowCal, createdAtCal)) {
            SimpleDateFormat format = new SimpleDateFormat("mm/dd", Locale.JAPAN);
            return format.format(createdAt);
        } else if (nowCal.get(Calendar.HOUR_OF_DAY) != createdAtCal.get(Calendar.HOUR_OF_DAY)) {
            return nowCal.get(Calendar.HOUR_OF_DAY) - createdAtCal.get(Calendar.HOUR_OF_DAY) + "h";
        } else if (nowCal.get(Calendar.MINUTE) > createdAtCal.get(Calendar.MINUTE)) {
            return nowCal.get(Calendar.MINUTE) - createdAtCal.get(Calendar.MINUTE) + "m";
        } else {
            String s = nowCal.get(Calendar.SECOND) - createdAtCal.get(Calendar.SECOND) + "s";
            return s.equals("-1s") ? "0s" : s;
        }

    }

    private static boolean isToday(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }


    private TwitterUtil() {
    }
}
