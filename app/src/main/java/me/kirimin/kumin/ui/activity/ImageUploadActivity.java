package me.kirimin.kumin.ui.activity;

import java.io.File;
import java.io.IOException;

import me.kirimin.kumin.AppPreferences;
import me.kirimin.kumin.R;
import me.kirimin.kumin.Twitter;
import me.kirimin.kumin.db.UserDAO;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ImageUploadActivity extends Activity {

    private final int REQUEST_IMAGE = 1;
    private final int REQUEST_CAMERA = 2;
    public static final String PREFERENCE_IMAGE_PATH = "path";

    private View mLayoutTop;
    private Button mButtonGallery, mButtonCamera;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_upload);

        mLayoutTop = findViewById(R.id.imageLayoutTop);
        mButtonGallery = (Button) findViewById(R.id.imageButtonGallery);
        mButtonCamera = (Button) findViewById(R.id.imageButtonCamera);

        mLayoutTop.setOnTouchListener(new OnTopTouchListener());
        mButtonGallery.setOnClickListener(new OnGalleryClickListener());
        mButtonCamera.setOnClickListener(new OnCameraClickListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        String path = "";
        if (requestCode == REQUEST_IMAGE) {
            Uri uri = data.getData();
            ContentResolver contentResolver = getContentResolver();
            String[] columns = { MediaStore.Images.Media.DATA };
            Cursor cursor = contentResolver.query(uri, columns, null, null, null);

            cursor.moveToFirst();
            path = cursor.getString(0);
        } else if (requestCode == REQUEST_CAMERA) {
            if (mUri != null) {
                path = mUri.getPath();
            }
        }

        AppPreferences appPref = new AppPreferences(this);
        Twitter twitter = new Twitter();
        twitter.addOnStatusUpdateListener(new OnStatusUpdateListener());
        twitter.setUser(new UserDAO(this).getUser(appPref.readCurrentUser()));
        try {
            twitter.updateStatus(appPref.readTweetTextCache(), path);
        } catch (IOException e) {
            Toast.makeText(this, R.string.twitter_exeption_image_too_big, Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private class OnGalleryClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }

    private class OnCameraClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/tmp.jpg"));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private class OnTopTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            finish();
            return false;
        }
    }

    private class OnStatusUpdateListener implements Twitter.OnStatusUpdateListener {

        @Override
        public void onStatusUpdate() {
            Toast.makeText(getApplicationContext(), R.string.layer_toast_tweet_ok, Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onFavorite() {}

        @Override
        public void onError() {
            Toast.makeText(getApplicationContext(), R.string.twitter_exeption_image_too_big, Toast.LENGTH_SHORT).show();
        }
    }
}