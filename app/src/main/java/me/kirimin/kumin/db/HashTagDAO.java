package me.kirimin.kumin.db;

import java.util.ArrayList;
import java.util.List;

import me.kirimin.kumin.db.DbHelper.HASH_TAG;
import me.kirimin.kumin.db.DbHelper.TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * HashTagDB操作クラス
 *
 * @author kirimin
 */
public class HashTagDAO {

    private final DbHelper helper;

    public HashTagDAO(Context context) {
        helper = new DbHelper(context);
    }

    /**
     * ハッシュタグを追加
     */
    public void addHashTag(String hashTag) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HASH_TAG.HASH_TAG.name(), hashTag);

        db.insert(TABLE.HASH_TAG.name(), null, values);
        db.close();
    }

    /**
     * ハッシュタグの取得
     *
     * @return 全ハッシュタグのリスト
     */
    public List<String> getHashTagList() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE.HASH_TAG.name(), new String[]{HASH_TAG.HASH_TAG.name()}, null, null, null, null, null);

        List<String> hashTags = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String hashTag = cursor.getString(0);
            hashTags.add(hashTag);
            cursor.moveToNext();
        }
        db.close();
        return hashTags;
    }

    /**
     * 指定されたハッシュタグをDBから削除
     */
    public void deleteHashTag(String hashTag) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE.HASH_TAG.name(), HASH_TAG.HASH_TAG.name() + " = ?", new String[]{hashTag});
        db.close();
    }
}