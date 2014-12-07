package me.kirimin.kumin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {

    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public AppPreferences(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean readIsShowAccountButton() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.setting_value_is_show_account_button), true);
    }

    public boolean readIsShowHashTagButton() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.setting_value_is_show_hashtag_button), true);
    }

    public boolean readIsShowMenuButton() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.setting_value_is_show_menu_button), true);
    }

    public boolean readIsCloseWhenTweet() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.setting_value_is_close_when_tweet), false);
    }

    public int readEditAlpha() {
        return mSharedPreferences.getInt(mContext.getString(R.string.setting_value_edit_aplha), 50);
    }

    public int readTimeLineAlpha() {
        return mSharedPreferences.getInt(mContext.getString(R.string.setting_value_timeline_aplha), 50);
    }

    public String readTweetTextCache() {
        return mSharedPreferences.getString(mContext.getString(R.string.setting_value_tweet_text_cache), "");
    }

    public void writeTweetTextCache(String tweetText) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mContext.getString(R.string.setting_value_tweet_text_cache), tweetText);
        editor.commit();
    }

    public int readTweetViewX() {
        return mSharedPreferences.getInt(mContext.getString(R.string.setting_value_tweet_view_x_cache), 0);
    }

    public void writeTweetViewX(int x) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mContext.getString(R.string.setting_value_tweet_view_x_cache), x);
        editor.commit();
    }

    public int readTweetViewY() {
        return mSharedPreferences.getInt(mContext.getString(R.string.setting_value_tweet_view_y_cache), 0);
    }

    public void writeTweetViewY(int y) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mContext.getString(R.string.setting_value_tweet_view_y_cache), y);
        editor.commit();
    }

    public String readCurrentUser() {
        return mSharedPreferences.getString(mContext.getString(R.string.setting_value_current_user), "");
    }

    public void writeCurrentUser(String user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mContext.getString(R.string.setting_value_current_user), user);
        editor.commit();
    }
}
