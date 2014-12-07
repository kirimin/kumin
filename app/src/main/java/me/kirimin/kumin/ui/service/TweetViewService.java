package me.kirimin.kumin.ui.service;

import java.util.List;

import me.kirimin.kumin.twitter.TwitterUtil;
import me.kirimin.kumin.ui.notification.AppNotificationBuilder;
import twitter4j.Status;

import me.kirimin.kumin.AppPreferences;
import me.kirimin.kumin.Constants;
import me.kirimin.kumin.R;
import me.kirimin.kumin.twitter.Twitter;
import me.kirimin.kumin.db.HashTagDAO;
import me.kirimin.kumin.db.User;
import me.kirimin.kumin.db.UserDAO;
import me.kirimin.kumin.ui.activity.ImageUploadActivity;
import me.kirimin.kumin.ui.adapter.TimeLineListViewAdapter;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 常駐レイヤーを表示させるためのService
 *
 * @author kirimin
 */
public class TweetViewService extends Service implements OnClickListener, OnTouchListener, OnItemClickListener, TextWatcher, OnItemLongClickListener {

    private static final int LAYOUT_TOP_ID = 1;

    private static final WindowManager.LayoutParams NOT_FOCUSABLE_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private static final WindowManager.LayoutParams FOCUSABLE_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 0, PixelFormat.TRANSLUCENT);

    private static final WindowManager.LayoutParams NOT_TOUCHABLE_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);

    private View mLayoutTop, mLayoutHandle, mLayoutTweetItems, mLayoutTimeLines, mButtonListviewResize;
    private ImageButton mButtonTweet, mButtonMenu;
    private Button mButtonOpen, mButtonClose, mButtonTL, mButtonAccount, mButtonHashTag;
    private EditText mEditTweet;
    private TextView mTextCharCount;
    private ListView mListTimeLine;

    /**
     * ドラッグで移動した位置覚えておく変数 *
     */
    private int mViewX, mViewY, mListViewResizeY;
    /**
     * タッチモード判定
     */
    private boolean isTouchable = true;

    private WindowManager mWindowManager;

    private Twitter mTwitter;
    private AppPreferences mAppPreferences;
    private TimeLineListViewAdapter mAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppPreferences = new AppPreferences(getApplicationContext());

        // オーバーレイViewの設定を行う
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        WindowManager.LayoutParams params = NOT_FOCUSABLE_PARAMS;
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        mLayoutTop = layoutInflater.inflate(R.layout.service_tweet_view, null);
        mLayoutTop.setId(LAYOUT_TOP_ID);
        mLayoutHandle = mLayoutTop.findViewById(R.id.tweetViewLayoutHandle);
        mLayoutTweetItems = mLayoutTop.findViewById(R.id.tweetViewLayoutTweetitems);
        mLayoutTimeLines = mLayoutTop.findViewById(R.id.tweetViewLayoutTimeLines);
        mButtonTweet = (ImageButton) mLayoutTop.findViewById(R.id.tweetViewButtonTweet);
        mButtonMenu = (ImageButton) mLayoutTop.findViewById(R.id.tweetViewButtonMenu);
        mButtonOpen = (Button) mLayoutTop.findViewById(R.id.tweetViewButtonOpen);
        mButtonTL = (Button) mLayoutTop.findViewById(R.id.tweetViewButtonTimeLineOpen);
        mButtonClose = (Button) mLayoutTop.findViewById(R.id.tweetViewButtonClose);
        mButtonAccount = (Button) mLayoutTop.findViewById(R.id.tweetViewButtonAccount);
        mButtonHashTag = (Button) mLayoutTop.findViewById(R.id.tweetViewButtonHashTag);
        mEditTweet = (EditText) mLayoutTop.findViewById(R.id.tweetViewEditTweet);
        mTextCharCount = (TextView) mLayoutTop.findViewById(R.id.tweetViewTextCharCount);
        mListTimeLine = (ListView) mLayoutTop.findViewById(R.id.tweetViewListTimeLine);
        mButtonListviewResize = (View) mLayoutTop.findViewById(R.id.tweetViewTimeLineResizeButton);

        mLayoutTop.setOnTouchListener(this);
        mLayoutHandle.setOnTouchListener(this);
        mButtonTweet.setOnClickListener(this);
        mButtonMenu.setOnClickListener(this);
        mButtonOpen.setOnClickListener(new OnOpenClickListener(mLayoutTweetItems));
        mButtonTL.setOnClickListener(new OnOpenClickListener(mLayoutTimeLines));
        mButtonClose.setOnClickListener(this);
        mButtonAccount.setOnClickListener(this);
        mButtonHashTag.setOnClickListener(this);
        mEditTweet.setOnTouchListener(this);
        mEditTweet.addTextChangedListener(this);
        mEditTweet.setText(mAppPreferences.readTweetTextCache());
        mTextCharCount.setText(String.valueOf(Constants.MAX_TWEET_LENGTH - mEditTweet.getText().length()));
        mListTimeLine.setOnItemClickListener(this);
        mListTimeLine.setOnItemLongClickListener(this);
        mListTimeLine.setOnTouchListener(this);
        mButtonListviewResize.setOnTouchListener(this);

        mViewX = mAppPreferences.readTweetViewX();
        mViewY = mAppPreferences.readTweetViewY();
        params.x = mViewX;
        params.y = mViewY;
        mWindowManager.addView(mLayoutTop, params);

        mEditTweet.setBackgroundColor(Color.argb(mAppPreferences.readEditAlpha(), 170, 170, 170));
        mListTimeLine.setBackgroundColor(Color.argb(mAppPreferences.readTimeLineAlpha(), 170, 170, 170));

        // Twitterインスタンス初期設定
        mTwitter = new Twitter();
        mTwitter.addOnStatusUpdateListener(new OnStatusUpdateListener());
        mTwitter.setStreamListener(new StreamListener());
        mAdapter = new TimeLineListViewAdapter(this, mTwitter);
        mListTimeLine.setAdapter(mAdapter);

        // デフォルトユーザーを設定
        UserDAO dao = new UserDAO(getApplicationContext());
        if (dao.getUsers().size() != 0) {
            String currentUserId = mAppPreferences.readCurrentUser();
            if (currentUserId.equals("")) {
                currentUserId = dao.getUsers().get(0).getId();
            }
            User user = dao.getUser(currentUserId);
            setUser(user);
        } else {
            Toast.makeText(this, R.string.account_not_find, Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        startForeground(1, AppNotificationBuilder.create(this));
        new StartStreamTask().execute(mTwitter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // タッチモード切り替え
        if (isTouchable) {
            mWindowManager.updateViewLayout(mLayoutTop, NOT_FOCUSABLE_PARAMS);
            mEditTweet.setVisibility(View.VISIBLE);
            mButtonListviewResize.setVisibility(View.VISIBLE);
            mButtonClose.setVisibility(View.VISIBLE);
            mButtonTweet.setVisibility(View.VISIBLE);
            mTextCharCount.setVisibility(View.VISIBLE);
            // 表示設定反映
            if (!mAppPreferences.readIsShowAccountButton()) {
                mButtonAccount.setVisibility(View.GONE);
            } else {
                mButtonAccount.setVisibility(View.VISIBLE);
            }
            if (!mAppPreferences.readIsShowHashTagButton()) {
                mButtonHashTag.setVisibility(View.GONE);
            } else {
                mButtonHashTag.setVisibility(View.VISIBLE);
            }
            if (!mAppPreferences.readIsShowMenuButton()) {
                mButtonMenu.setVisibility(View.GONE);
            } else {
                mButtonMenu.setVisibility(View.VISIBLE);
            }

            mButtonOpen.setText(getString(R.string.char_minimize));
        } else {
            NOT_TOUCHABLE_PARAMS.x = NOT_FOCUSABLE_PARAMS.x;
            NOT_TOUCHABLE_PARAMS.y = NOT_FOCUSABLE_PARAMS.y;
            mWindowManager.updateViewLayout(mLayoutTop, NOT_TOUCHABLE_PARAMS);
            mButtonListviewResize.setVisibility(View.GONE);
            mButtonClose.setVisibility(View.GONE);
            mButtonHashTag.setVisibility(View.GONE);
            mButtonMenu.setVisibility(View.GONE);
            mButtonTweet.setVisibility(View.GONE);
            mButtonAccount.setVisibility(View.GONE);
            mTextCharCount.setVisibility(View.GONE);

            mLayoutTimeLines.setVisibility(View.VISIBLE);
            mButtonTL.setText(getString(R.string.char_minimize));
            new StartStreamTask().execute(mTwitter);

            mEditTweet.setVisibility(View.GONE);
            mButtonOpen.setText(getString(R.string.char_open));
        }
        isTouchable = !isTouchable;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new StopStreamTask().execute(mTwitter);
        mWindowManager.removeViewImmediate(mLayoutTop);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tweetViewButtonClose:
                // 閉じるボタン
                mAppPreferences.writeTweetViewX(mViewX);
                mAppPreferences.writeTweetViewY(mViewY);
                stopSelf();

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(1, AppNotificationBuilder.create(TweetViewService.this));
                break;

            case R.id.tweetViewButtonAccount:
                // 投稿するアカウントを入れ替える
                User nextUser = TwitterUtil.searchNextUser(new UserDAO(this).getUsers(), mButtonAccount.getText().toString());
                if (nextUser != null) {
                    setUser(nextUser);
                }
                break;

            case R.id.tweetViewButtonHashTag:
                // ハッシュタグを入れ替える
                HashTagDAO hashTagDao = new HashTagDAO(TweetViewService.this);
                final List<String> hashTagList = hashTagDao.getHashTagList();
                hashTagList.add(getString(R.string.char_hashTag));
                for (int i = 0; i < hashTagList.size(); i++) {
                    if (!hashTagList.get(i).equals(mButtonHashTag.getText()))
                        continue;
                    if (hashTagList.size() == i + 1) {
                        mButtonHashTag.setText(hashTagList.get(0));
                    } else {
                        mButtonHashTag.setText(hashTagList.get(i + 1));
                    }
                    break;
                }
                break;

            case R.id.tweetViewButtonMenu:
                // メニューボタン
                Intent intent = new Intent(TweetViewService.this, ImageUploadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.tweetViewButtonTweet:
                // ツイートボタン
                String tweet = mEditTweet.getText().toString();
                if (!mButtonHashTag.getText().toString().equals(getString(R.string.char_hashTag))) {
                    tweet += " " + mButtonHashTag.getText();
                }
                Toast.makeText(TweetViewService.this, getString(R.string.layer_toast_tweeting), Toast.LENGTH_SHORT).show();
                mTwitter.updateStatus(tweet);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.tweetViewEditTweet:
            case R.id.tweetViewListTimeLine:
                // 範囲内タッチ
                WindowManager.LayoutParams innerView = FOCUSABLE_PARAMS;
                innerView.x = mViewX;
                innerView.y = mViewY;
                mWindowManager.updateViewLayout(mLayoutTop, innerView);
                return false;

            case LAYOUT_TOP_ID:
                // 範囲外タッチ
                WindowManager.LayoutParams outerView = NOT_FOCUSABLE_PARAMS;
                outerView.x = mViewX;
                outerView.y = mViewY;

                // 更新
                mWindowManager.updateViewLayout(mLayoutTop, outerView);
                return false;

            case R.id.tweetViewLayoutHandle:
                // Viewドラッグ処理
                Display display = mWindowManager.getDefaultDisplay();
                if (mLayoutTimeLines.getVisibility() == View.GONE) {
                    mViewY = (int) event.getRawY() - display.getHeight() / 2 - dp2Px(50);
                } else if (mLayoutTimeLines.getVisibility() == View.VISIBLE && mLayoutTweetItems.getVisibility() == View.VISIBLE) {
                    mViewY = (int) event.getRawY() - display.getHeight() / 2 - dp2Px(50)
                            + mListTimeLine.getHeight() / 2
                            + mLayoutTweetItems.getHeight() / 2
                            - mLayoutHandle.getHeight() / 2;
                } else {
                    mViewY = (int) event.getRawY() - display.getHeight() / 2 - dp2Px(50)
                            + mListTimeLine.getHeight() / 2
                            - mLayoutHandle.getHeight() / 2;
                }
                mViewX = mLayoutTweetItems.getVisibility() == View.GONE && mLayoutTimeLines.getVisibility() == View.GONE
                        ? (int) event.getRawX() - display.getWidth() / 2
                        : (int) event.getRawX() - display.getWidth() / 2 - dp2Px(140);

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // タッチ位置にビューを設定
                    WindowManager.LayoutParams params = NOT_FOCUSABLE_PARAMS;
                    params.x = mViewX;
                    params.y = mViewY;
                    mWindowManager.updateViewLayout(mLayoutTop, params);
                }

                return true;

            case R.id.tweetViewTimeLineResizeButton:
                // ツイートViewリサイズ
                int drag = (int) event.getRawY() - mListViewResizeY;
                if (drag < dp2Px(200) && drag > -dp2Px(200) && event.getAction() == MotionEvent.ACTION_MOVE) {
                    int height = mListTimeLine.getHeight() + drag;
                    if (height > dp2Px(100)) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mListTimeLine.getWidth(), height);
                        mListTimeLine.setLayoutParams(params);
                    }
                }
                mListViewResizeY = (int) event.getRawY();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Status status = (Status) parent.getAdapter().getItem(position);
        mEditTweet.setText(getString(R.string.char_replay) + status.getUser().getScreenName() + " " + mEditTweet.getText());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(TweetViewService.this, R.string.layer_toast_favoriting, Toast.LENGTH_SHORT).show();
        Status status = (Status) parent.getAdapter().getItem(position);
        mTwitter.doFavorite(status.getId());
        return true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        int charCount = Constants.MAX_TWEET_LENGTH - mEditTweet.getText().length();
        mTextCharCount.setText(String.valueOf(charCount));
        mAppPreferences.writeTweetTextCache(s.toString());
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * 投稿するユーザーを設定する。
     *
     * @param user 設定するユーザー
     */
    private void setUser(User user) {
        mTwitter.setUser(user);
        if (mLayoutTimeLines.getVisibility() == View.VISIBLE) {
            new ReStartStreamTask().execute(mTwitter);
        }
        mButtonAccount.setText(user.getSName());
        mAppPreferences.writeCurrentUser(user.getId());
    }

    /**
     * dpをpxに変換する
     *
     * @param dp dp
     * @return px
     */
    private int dp2Px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * 最小化ボタン押下時処理
     */
    private class OnOpenClickListener implements OnClickListener {

        private View actionTargetView;

        public OnOpenClickListener(View actionTargetView) {
            this.actionTargetView = actionTargetView;
        }

        @Override
        public void onClick(View view) {
            Button buttonOpen = (Button) view;
            if (actionTargetView.getVisibility() == View.VISIBLE) {
                actionTargetView.setVisibility(View.GONE);
                buttonOpen.setText("□");
                if (mLayoutTweetItems.getVisibility() == View.GONE && mLayoutTimeLines.getVisibility() == View.GONE) {
                    updateViewLayout(140);
                }
                if (actionTargetView == mLayoutTimeLines) {
                    new StopStreamTask().execute(mTwitter);
                }
            } else {
                actionTargetView.setVisibility(View.VISIBLE);
                buttonOpen.setText("‐");
                if (mLayoutTweetItems.getVisibility() == View.GONE || mLayoutTimeLines.getVisibility() == View.GONE) {
                    updateViewLayout(-140);
                }
                if (actionTargetView == mLayoutTimeLines) {
                    new StartStreamTask().execute(mTwitter);
                }
            }
        }

        private void updateViewLayout(int addDp) {
            WindowManager.LayoutParams params = NOT_FOCUSABLE_PARAMS;
            params.x = mViewX += dp2Px(addDp);
            params.y = mViewY;

            // 更新
            mWindowManager.updateViewLayout(mLayoutTop, params);
        }
    }

    /**
     * Tweet投稿時の処理
     */
    private class OnStatusUpdateListener implements Twitter.OnStatusUpdateListener {

        @Override
        public void onStatusUpdate() {
            Toast.makeText(TweetViewService.this, R.string.layer_toast_tweet_ok, Toast.LENGTH_SHORT).show();
            mEditTweet.setText("");

            if (mAppPreferences.readIsCloseWhenTweet()) {
                mLayoutTop.findViewById(R.id.tweetViewLayoutTweetitems).setVisibility(View.GONE);
                mButtonOpen.setText("□");
                WindowManager.LayoutParams params = NOT_FOCUSABLE_PARAMS;
                params.x = mViewX += dp2Px(140);
                params.y = mViewY;

                // 更新
                mWindowManager.updateViewLayout(mLayoutTop, params);
            }
        }

        @Override
        public void onFavorite() {
            Toast.makeText(TweetViewService.this, R.string.layer_toast_favo_ok, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError() {
            Toast.makeText(TweetViewService.this, R.string.twitter_exeption_network, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * UserStream取得時の処理
     */
    private class StreamListener implements Twitter.StreamListener {

        @Override
        public void onStatus(Status status) {
            mAdapter.insert(status, 0);
            if (mAdapter.getCount() > 50) {
                mAdapter.remove(mAdapter.getItem(mAdapter.getCount() - 1));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Stream開始
     */
    private static class StartStreamTask extends AsyncTask<Twitter, String, String> {

        @Override
        protected String doInBackground(Twitter... params) {
            params[0].startUserStream();
            return null;
        }
    }

    /**
     * Stream停止
     */
    private static class StopStreamTask extends AsyncTask<Twitter, String, String> {

        @Override
        protected String doInBackground(Twitter... params) {
            params[0].stopUserStream();
            return null;
        }
    }

    /**
     * Streamリスタート
     */
    private static class ReStartStreamTask extends AsyncTask<Twitter, String, String> {

        @Override
        protected String doInBackground(Twitter... params) {
            params[0].stopUserStream();
            while (params[0].startUserStream()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return null;
        }
    }
}