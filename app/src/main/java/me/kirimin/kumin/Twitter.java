package me.kirimin.kumin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Handler;

import me.kirimin.kumin.db.User;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.ConnectionLifeCycleListener;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamAdapter;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Twitter4Jライブラリをラップする
 *
 * @author kirimin
 */
public class Twitter {

    private AsyncTwitter mTwitter = new AsyncTwitterFactory().getInstance();
    private TwitterStream mStream = new TwitterStreamFactory().getInstance();
    private UserStreamListener mStreamListener;

    private Handler mHandler = new Handler();
    private RequestToken mRequestToken;

    private boolean isFinishedClose = true;

    public Twitter() {
        mTwitter.setOAuthConsumer(Consumer.K, Consumer.S);
        mStream.setOAuthConsumer(Consumer.K, Consumer.S);
    }

    /**
     * ユーザーを設定する
     *
     * @param user
     */
    public void setUser(User user) {
        AccessToken token = new AccessToken(user.getToken(), user.getSecret());
        mTwitter.setOAuthAccessToken(token);
        mStream.setOAuthAccessToken(token);
    }

    /**
     * 投稿. OnStatusUpdateListenerで結果を取得出来る
     *
     * @param tweet 投稿内容
     */
    public void updateStatus(String tweet) {
        mTwitter.updateStatus(tweet);
    }

    /**
     * 画像つき投稿. OnStatusUpdateListenerで結果を取得出来る
     *
     * @param tweet     投稿内容
     * @param imagePath 画像の保存パス
     * @throws IOException
     */
    public void updateStatus(String tweet, String imagePath) throws IOException {
        StatusUpdate status = new StatusUpdate(tweet);
        File file = new File(imagePath);
        if (file.length() >= 3145728)
            throw new IOException();

        status.media(file);
        mTwitter.updateStatus(status);
    }

    /**
     * お気に入り登録/解除
     *
     * @param user
     */
    public void doFavorite(long userId) {
        mTwitter.createFavorite(userId);
    }

    /**
     * ユーザーストリームの取得を開始
     */
    public synchronized boolean startUserStream() {
        if (isFinishedClose) {
            isFinishedClose = false;
            mStream.user();
            return true;
        }
        return false;
    }

    /**
     * ユーザーストリームの取得を停止
     */
    public synchronized void stopUserStream() {
        mStream.shutdown();
    }

    /**
     * リクエストトークンを取得する
     */
    public void getOAuthRequestTokenAsync() {
        mTwitter.getOAuthRequestTokenAsync();
    }

    public Uri getAuthorizationUri() {
        if (mRequestToken == null)
            throw new NullPointerException("token is empty");
        return Uri.parse(mRequestToken.getAuthorizationURL());
    }

    public void getOAuthAccessToken(String pincode) {
        mTwitter.getOAuthAccessTokenAsync(pincode);
    }

    public void setOnOAuthListener(final OnOAuthListener listener) {
        mTwitter.addListener(new TwitterAdapter() {
            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                mRequestToken = token;
            }

            @Override
            public void gotOAuthAccessToken(final AccessToken accessToken) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String userId = String.valueOf(accessToken.getUserId());
                        String sName = accessToken.getScreenName();
                        String token = accessToken.getToken();
                        String secret = accessToken.getTokenSecret();
                        listener.gotOAuthAccessToken(new User(userId, sName, token, secret));
                    }
                });
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                te.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError();
                    }
                });
            }
        });
    }

    public void setStreamListener(final StreamListener listener) {
        mStream.clearListeners();
        mStreamListener = new UserStreamAdapter() {
            @Override
            public void onStatus(final Status status) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStatus(status);
                    }
                });
            }
        };
        mStream.addListener(mStreamListener);
        mStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListener() {

            @Override
            public void onDisconnect() {
            }

            @Override
            public void onConnect() {
            }

            @Override
            public void onCleanUp() {
                isFinishedClose = true;
            }
        });
    }

    public void addOnStatusUpdateListener(final OnStatusUpdateListener listener) {
        mTwitter.addListener(new TwitterAdapter() {
            @Override
            public void updatedStatus(Status status) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStatusUpdate();
                    }
                });
            }

            @Override
            public void createdFavorite(Status status) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFavorite();
                    }
                });
            }

            @Override
            public void onException(final TwitterException te, TwitterMethod method) {
                te.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError();
                    }
                });
            }
        });
    }

    public String getTimeStamp(Date createdAt) {
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

    private boolean isToday(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * ツイート時に呼び出されるリスナー
     */
    public interface OnStatusUpdateListener {
        public void onStatusUpdate();

        public void onFavorite();

        public void onError();
    }

    /**
     * ストリーム取得時に呼び出されるリスナー
     */
    public interface StreamListener {
        public void onStatus(Status status);
    }

    public interface OnOAuthListener {
        public void gotOAuthAccessToken(User user);

        public void onError();
    }
}