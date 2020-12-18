package com.example.video;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLHelperRating extends SQLiteOpenHelper {
    static final String DB_NAME = "Rating";
    static final String DB_TABLE_RATING = "RatingVideo";
    static final int DB_VERSION = DeFile.DB_VERSION;
    SQLiteDatabase sqLiteDatabase;
    ContentValues contentValues;
    Cursor cursor;
    Context context;

    public SQLHelperRating(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query="CREATE TABLE RatingVideo(" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "rate FLOAT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i!=i1){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DB_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public boolean checkRated(int id){
        sqLiteDatabase=getWritableDatabase();
        cursor=sqLiteDatabase.rawQuery("Select * from "+DB_TABLE_RATING+" where id="+id, null);
        if (cursor.getCount()==1)
            return true;
        return false;
    }

    public float getRating(int id){
        List<RatingVideo> list=new ArrayList<>();
        sqLiteDatabase=getWritableDatabase();
        cursor=sqLiteDatabase.rawQuery("Select * from "+DB_TABLE_RATING+" where id="+id, null);
        while(cursor.moveToNext()){
            int id1=cursor.getInt(cursor.getColumnIndex("id"));
            float rate=cursor.getFloat(cursor.getColumnIndex("rate"));
            RatingVideo video=new RatingVideo(id1, rate);
            list.add(video);
        }
        return list.get(0).getRate();
    }

    public void insert(RatingVideo video){
        sqLiteDatabase=getWritableDatabase();
        contentValues=new ContentValues();

        contentValues.put("id", video.getId());
        contentValues.put("rate", video.getRate());

        sqLiteDatabase.insert(DB_TABLE_RATING, null, contentValues);
    }

    public void update(int id, float rate){
        sqLiteDatabase=getWritableDatabase();
        contentValues=new ContentValues();

        contentValues.put("rate", rate);

        sqLiteDatabase.update(DB_TABLE_RATING, contentValues, "id=?", new String[]{String.valueOf(id)});
    }
}
