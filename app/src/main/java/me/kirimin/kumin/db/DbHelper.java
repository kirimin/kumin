package me.kirimin.kumin.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    /**
     * テーブル名
     */
    public enum TABLE {
        USER, HASH_TAG
    }

    /**
     * USERテーブルの各column
     */
    public enum USER {
        USER_ID, USER_S_NAME, USER_TOKEN, USER_SECRET;

        /**
         * USERテーブルの全項目名を配列で返す
         */
        public static String[] columns() {
            List<String> columns = new ArrayList<String>();
            for (USER column : USER.values()) {
                columns.add(column.name());
            }

            return columns.toArray(new String[0]);
        }
    }

    public enum HASH_TAG {HASH_TAG}

    public DbHelper(Context context) {
        super(context, "hitorigoto.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);
        createHashTagTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 自動生成されたメソッド・スタブ
    }

    /**
     * USERテーブルの作成
     */
    private void createUserTable(SQLiteDatabase db) {
        String sql = "create table " + TABLE.USER + " ( "
                + USER.USER_ID + " PRIMARY KEY, "
                + USER.USER_S_NAME + ", "
                + USER.USER_TOKEN + ", "
                + USER.USER_SECRET + ");";
        db.execSQL(sql);
    }

    /**
     * HASH_TAGテーブルの作成
     */
    private void createHashTagTable(SQLiteDatabase db) {
        String sql = "create table " + TABLE.HASH_TAG + " ( "
                + HASH_TAG.HASH_TAG + " PRIMARY KEY);";
        db.execSQL(sql);
    }
}
