package me.kirimin.kumin.db;

import java.util.ArrayList;
import java.util.List;

import me.kirimin.kumin.db.DbHelper.TABLE;
import me.kirimin.kumin.db.DbHelper.USER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * UserDB操作クラス
 *
 * @author kirimin
 */
public class UserDAO {

    private final DbHelper helper;

    public UserDAO(Context context) {
        helper = new DbHelper(context);
    }

    /**
     * ユーザーを追加
     */
    public void addUser(User user) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER.USER_ID.name(), user.getId());
        values.put(USER.USER_S_NAME.name(), user.getSName());
        values.put(USER.USER_TOKEN.name(), user.getToken());
        values.put(USER.USER_SECRET.name(), user.getSecret());

        db.insert(TABLE.USER.name(), null, values);
        db.close();
    }

    /**
     * 全ユーザーの取得
     *
     * @return 全ユーザーのリスト
     */
    public List<User> getUsers() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE.USER.name(), USER.columns(), null, null, null, null, null);

        List<User> users = new ArrayList<User>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            String sName = cursor.getString(1);
            String token = cursor.getString(2);
            String secret = cursor.getString(3);
            users.add(new User(id, sName, token, secret));
            cursor.moveToNext();
        }
        db.close();
        return users;
    }

    /**
     * 指定されたIDのユーザーを取得
     *
     * @param userId アカウント固有のユーザーID(数字列)
     * @return 取得したユーザー
     * @throws IllegalArgumentException 指定されたIDのユーザーが存在しない
     */
    public User getUser(String userId) throws IllegalArgumentException {
        SQLiteDatabase db = helper.getReadableDatabase();
        String whare = USER.USER_ID + " = ?";

        Cursor cursor =
                db.query(TABLE.USER.name(), USER.columns(), whare, new String[]{userId}, null, null, "1");

        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            String sName = cursor.getString(1);
            String token = cursor.getString(2);
            String secret = cursor.getString(3);
            db.close();
            return new User(id, sName, token, secret);
        } else {
            db.close();
            throw new IllegalArgumentException("該当データが見つかりませんでした");
        }
    }

    /**
     * 指定されたIDのユーザーをDBから削除
     *
     * @param userId アカウント固有のユーザーID(数字列)
     */
    public void deleteUser(String userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE.USER.name(), USER.USER_ID.name() + " = ?", new String[]{userId});
        db.close();
    }
}