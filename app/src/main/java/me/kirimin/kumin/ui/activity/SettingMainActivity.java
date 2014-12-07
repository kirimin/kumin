package me.kirimin.kumin.ui.activity;

import java.util.List;

import me.kirimin.kumin.AppPreferences;
import me.kirimin.kumin.R;
import me.kirimin.kumin.db.HashTagDAO;
import me.kirimin.kumin.model.User;
import me.kirimin.kumin.db.UserDAO;
import me.kirimin.kumin.ui.notification.AppNotificationBuilder;
import me.kirimin.kumin.ui.service.TweetViewService;

import android.graphics.Color;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 設定画面
 *
 * @author kirimin
 */
public class SettingMainActivity extends ActionBarActivity {

    private SharedPreferences mSharedPreferences;

    private ImageView startButtonImageView;
    private TextView startButtonTextView;
    private LinearLayout accountListLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setting);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        startButtonImageView = (ImageView) findViewById(R.id.settingStartButtonImageView);
        startButtonTextView = (TextView) findViewById(R.id.settingStartButtonTextView);
        accountListLayout = (LinearLayout) findViewById(R.id.settingAccountListLayout);

        startButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRunning = switchNotification();
                updateStartButtonViews(isRunning);
            }
        });

        findViewById(R.id.settingAddAcountButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingMainActivity.this, OAuthActivity.class));
            }
        });
        findViewById(R.id.settingAboutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingMainActivity.this, AboutActivity.class));
            }
        });
        findViewById(R.id.settingAddHashTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> hashTags = new HashTagDAO(SettingMainActivity.this).getHashTagList();
                hashTags.add(getString(R.string.setting_main_add_hashtag));

                OnClickListener onUserClickListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == hashTags.size() - 1) {
                            createAddHashTagDialog().show();
                        } else {
                            createHashTagDeleteDialog(hashTags.get(which)).show();
                        }
                    }
                };

                AlertDialog accountSettingDialog = new AlertDialog.Builder(SettingMainActivity.this)
                        .setTitle(R.string.setting_main_hashtag)
                        .setItems(hashTags.toArray(new String[hashTags.size()]), onUserClickListener)
                        .create();

                accountSettingDialog.show();
            }
        });
        CheckBox minimizeWhenTweetCheckBox = (CheckBox) findViewById(R.id.settingMinimizeWhenTweetCheckBox);
        minimizeWhenTweetCheckBox.setChecked(mSharedPreferences.getBoolean(getString(R.string.setting_value_is_close_when_tweet), false));
        minimizeWhenTweetCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferences.edit().putBoolean(getString(R.string.setting_value_is_close_when_tweet), isChecked).commit();
            }
        });
        CheckBox showChangeAccountButtonCheckBox = (CheckBox) findViewById(R.id.settingShowChengeAccountButtonCheckBox);
        showChangeAccountButtonCheckBox.setChecked(mSharedPreferences.getBoolean(getString(R.string.setting_value_is_show_account_button), true));
        showChangeAccountButtonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferences.edit().putBoolean(getString(R.string.setting_value_is_show_account_button), isChecked).commit();
            }
        });
        CheckBox showOptionButtonCheckBox = (CheckBox) findViewById(R.id.settingShowOptionButtonCheckBox);
        showOptionButtonCheckBox.setChecked(mSharedPreferences.getBoolean(getString(R.string.setting_value_is_show_menu_button), true));
        showOptionButtonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferences.edit().putBoolean(getString(R.string.setting_value_is_show_menu_button), isChecked).commit();
            }
        });
        CheckBox showHashTagButtonCheckBox = (CheckBox) findViewById(R.id.settingShowHashTagButtonCheckBox);
        showHashTagButtonCheckBox.setChecked(mSharedPreferences.getBoolean(getString(R.string.setting_value_is_show_hashtag_button), true));
        showHashTagButtonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedPreferences.edit().putBoolean(getString(R.string.setting_value_is_show_hashtag_button), isChecked).commit();
            }
        });

        SeekBar alphaEditSpaceSeekBar = (SeekBar) findViewById(R.id.settingBackgroundAlphaOnEditSpaceSeekBar);
        alphaEditSpaceSeekBar.setProgress(mSharedPreferences.getInt(getString(R.string.setting_value_edit_aplha), 50));
        final View editSpaceSampleView = findViewById(R.id.settingBackgroundAlphaOnEditSpaceSample);
        editSpaceSampleView.setBackgroundColor(Color.argb(mSharedPreferences.getInt(getString(R.string.setting_value_edit_aplha), 50), 170, 170, 170));
        alphaEditSpaceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editSpaceSampleView.setBackgroundColor(Color.argb(seekBar.getProgress(), 170, 170, 170));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSharedPreferences.edit().putInt(getString(R.string.setting_value_edit_aplha), seekBar.getProgress()).commit();
            }
        });

        SeekBar alphaTimeLineSpaceSeedBar = (SeekBar) findViewById(R.id.settingBackgroundAlphaOnTimelineSpaceSeekBar);
        alphaTimeLineSpaceSeedBar.setProgress(mSharedPreferences.getInt(getString(R.string.setting_value_timeline_aplha), 50));
        final View timeLineSpaceSampleView = findViewById(R.id.settingBackgroundAlphaOnTimelineSpaceSample);
        timeLineSpaceSampleView.setBackgroundColor(Color.argb(mSharedPreferences.getInt(getString(R.string.setting_value_timeline_aplha), 50), 170, 170, 170));
        alphaTimeLineSpaceSeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeLineSpaceSampleView.setBackgroundColor(Color.argb(seekBar.getProgress(), 170, 170, 170));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSharedPreferences.edit().putInt(getString(R.string.setting_value_timeline_aplha), seekBar.getProgress()).commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAccountListViews(new UserDAO(SettingMainActivity.this).getUsers());
        updateStartButtonViews(mSharedPreferences.getBoolean(getString(R.string.setting_value_is_running), false));
    }

    /**
     * 常駐開始ボタンのレイアウトを更新する
     *
     * @param isRunning 常駐状態か停止状態か
     */
    private void updateStartButtonViews(boolean isRunning) {
        startButtonImageView.setSelected(isRunning);
        startButtonImageView.setImageResource(isRunning ? R.drawable.ic_action_stop : R.drawable.ic_action_play);
        startButtonTextView.setText(isRunning ? R.string.setting_main_stop : R.string.setting_main_start);
        startButtonTextView.setTextColor(isRunning ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.purple));
    }

    /**
     * アカウントリストのレイアウトを更新する
     *
     * @param users ユーザー情報の一覧
     */
    private void updateAccountListViews(List<User> users) {
        accountListLayout.removeAllViews();
        findViewById(R.id.settingAccountTitleTextView).setVisibility(users.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.settingAccountEmptyTextView).setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);

        for (final User user : users) {
            View accountRow = LayoutInflater.from(this).inflate(R.layout.activity_setting_row_accounts, null);
            TextView nameTextView = (TextView) accountRow.findViewById(R.id.settingAccountNameTextView);
            Button deleteButton = (Button) accountRow.findViewById(R.id.settingAccountNameDeleteButton);
            nameTextView.setText("@" + user.getSName());
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildAccountDeleteDialog(user).show();
                }
            });
            accountListLayout.addView(accountRow);
        }
    }

    /**
     * NotificationのONOFFを切り替える
     */
    private boolean switchNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        boolean isRunning = mSharedPreferences.getBoolean(getString(R.string.setting_value_is_running), false);
        if (isRunning) {
            Intent intent = new Intent(SettingMainActivity.this, TweetViewService.class);
            stopService(intent);
            manager.cancel(1);
            mSharedPreferences.edit().putBoolean(getString(R.string.setting_value_is_running), false).commit();
            return false;
        } else {
            manager.notify(1, AppNotificationBuilder.create(SettingMainActivity.this));
            mSharedPreferences.edit().putBoolean(getString(R.string.setting_value_is_running), true).commit();
            return true;
        }
    }

    private AlertDialog buildAccountDeleteDialog(final User user) {
        OnClickListener onPositiveListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserDAO dao = new UserDAO(SettingMainActivity.this);
                dao.deleteUser(user.getId());
                Toast.makeText(SettingMainActivity.this, R.string.delete_aount_dialog_deleted, Toast.LENGTH_SHORT).show();

                List<User> users = dao.getUsers();
                new AppPreferences(SettingMainActivity.this).writeCurrentUser(users.isEmpty() ? "" : users.get(0).getId());
                updateAccountListViews(users);
            }
        };

        OnClickListener onNegativeListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        AlertDialog accountDeleteDialog = new AlertDialog.Builder(SettingMainActivity.this)
                .setMessage(user.getSName() + getString(R.string.delete_aount_dialog_message))
                .setPositiveButton(R.string.delete_aount_dialog_positive, onPositiveListener)
                .setNegativeButton(R.string.delete_aount_dialog_negative, onNegativeListener)
                .create();

        return accountDeleteDialog;
    }

    private AlertDialog createHashTagDeleteDialog(final String hashTag) {
        OnClickListener onPositiveListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                HashTagDAO dao = new HashTagDAO(SettingMainActivity.this);
                dao.deleteHashTag(hashTag);
                Toast.makeText(SettingMainActivity.this, R.string.delete_aount_dialog_deleted, Toast.LENGTH_SHORT).show();
            }
        };

        AlertDialog accountDeleteDialog = new AlertDialog.Builder(SettingMainActivity.this)
                .setMessage(hashTag + getString(R.string.delete_aount_dialog_message))
                .setPositiveButton(R.string.delete_aount_dialog_positive, onPositiveListener)
                .setNegativeButton(R.string.delete_aount_dialog_negative, null)
                .create();

        return accountDeleteDialog;
    }

    private AlertDialog createAddHashTagDialog() {
        final EditText editHashTag = new EditText(SettingMainActivity.this);
        editHashTag.setText("#");

        OnClickListener onPositiveListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                HashTagDAO dao = new HashTagDAO(SettingMainActivity.this);
                dao.addHashTag(editHashTag.getText().toString());
            }
        };

        AlertDialog addHashTagDialog = new AlertDialog.Builder(SettingMainActivity.this)
                .setTitle(R.string.setting_main_add_hashtag)
                .setPositiveButton(R.string.setting_main_add_hashtag_button, onPositiveListener)
                .setView(editHashTag)
                .create();

        return addHashTagDialog;
    }
}