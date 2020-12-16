package com.example.video;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLHelperHistory extends SQLiteOpenHelper {

    static final String DB_NAME = DeFile.HISTORY_DB;
    static final String DB_TABLE_VIDEO = DeFile.DB_TABLE_VIDEO;
    static final int DB_VERSION = DeFile.DB_VERSION;
    SQLiteDatabase sqLiteDatabase;
    ContentValues contentValues;
    Cursor cursor;
    Context context;

    public SQLHelperHistory(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE Videos(" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "title TEXT," +
                "avatar TEXT," +
                "file_mp4 TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i != i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public boolean checkExist(HotVideos videos) {
        sqLiteDatabase=getWritableDatabase();
        cursor=sqLiteDatabase.rawQuery("Select * from "+DB_TABLE_VIDEO+" where id="+videos.getId(), null);
        if (cursor.getCount()==1)
            return true;
        return false;
    }

    public void insertVideo(HotVideos videos) {
        if (checkExist(videos))
            deleteVideo(videos.getId());
        sqLiteDatabase = getWritableDatabase();
        contentValues = new ContentValues();

        contentValues.put(DeFile.ID, videos.getId());
        contentValues.put(DeFile.TITLE, videos.getTitle());
        contentValues.put(DeFile.AVATAR, videos.getAvatar());
        contentValues.put(DeFile.FILE_MP4, videos.getFile_mp4());

        sqLiteDatabase.insert(DB_TABLE_VIDEO, null, contentValues);
    }

    public void deleteVideo(int id) {
        sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.delete(DB_TABLE_VIDEO, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteAllVideo() {
        sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(DB_TABLE_VIDEO, null, null);
    }


    public List<HotVideos> getAllFoods() {
        List<HotVideos> list = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();

        HotVideos videos;
        cursor = sqLiteDatabase.query(false, DB_TABLE_VIDEO, null, null,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DeFile.ID));
            String title = cursor.getString(cursor.getColumnIndex(DeFile.TITLE));
            String avatar = cursor.getString(cursor.getColumnIndex(DeFile.AVATAR));
            String file_mp4 = cursor.getString(cursor.getColumnIndex(DeFile.FILE_MP4));

            videos = new HotVideos(id, title, avatar, file_mp4);
            list.add(videos);
        }

        return list;
    }

    private void closeDB() {
        if (contentValues != null) contentValues.clear();
        if (cursor != null) cursor.close();
        if (sqLiteDatabase != null) sqLiteDatabase.close();
    }
}
