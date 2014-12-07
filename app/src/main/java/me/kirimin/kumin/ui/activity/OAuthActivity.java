package me.kirimin.kumin.ui.activity;

import me.kirimin.kumin.R;
import me.kirimin.kumin.Twitter;
import me.kirimin.kumin.db.User;
import me.kirimin.kumin.db.UserDAO;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/***
 * OAuth認証画面
 * 
 * @author kirimin
 * 
 */
public class OAuthActivity extends ActionBarActivity {

    private Button mButtonAccessTwitter, mButtonLogin;

    private Twitter mTwitter;
    private ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        mTwitter = new Twitter();
        mTwitter.setOnOAuthListener(new OnOAuthListener());
        mTwitter.getOAuthRequestTokenAsync();

        mButtonAccessTwitter = (Button) findViewById(R.id.oauthButtonAccesstwitter);
        mButtonLogin = (Button) findViewById(R.id.oauthButtonLogin);

        mButtonAccessTwitter.setOnClickListener(new OnAccessTwitterButtonClickListener());
        mButtonLogin.setOnClickListener(new OnLoginButtonClickListener());

        mDialog = new ProgressDialog(OAuthActivity.this);
        mDialog.setTitle(R.string.oauth_dialog_access_twitter);
    }

    /***
     * Twitterアクセスボタンクリック時処理
     */
    private class OnAccessTwitterButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, mTwitter.getAuthorizationUri());
                startActivity(intent);
            } catch (NullPointerException e) {
                mTwitter.getOAuthRequestTokenAsync();
                Toast.makeText(OAuthActivity.this, R.string.twitter_exeption_network, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***
     * ログインボタンクリック時処理
     */
    private class OnLoginButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            EditText editPinCode = (EditText) findViewById(R.id.oauthEditPincode);
            mTwitter.getOAuthAccessToken(editPinCode.getText().toString());
            mDialog.show();
        }
    }

    private class OnOAuthListener implements Twitter.OnOAuthListener {

        @Override
        public void gotOAuthAccessToken(User user) {
            // Access Token 取得成功。呼び出し元に Access Token を返す
            UserDAO dao = new UserDAO(OAuthActivity.this);
            dao.addUser(user);
            Toast.makeText(OAuthActivity.this, R.string.oauth_mess_add_account, Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }

        @Override
        public void onError() {
            Toast.makeText(OAuthActivity.this, R.string.twitter_exeption_auth, Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }
}