package com.example.newsapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

//extends SQLiteOpenHelper to create/upgrade the database
public class MyOpener extends SQLiteOpenHelper {


    //Database name
    protected final static String DATABASE_NAME = "NewsDB";
    //Database version
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "NEWS_LIST";
    //Column names
    public final static String COL_TITLE = "TITLE";
    public final static String COL_ID = "ID";
    public final static String COL_LINK = "LINK";
    public final static String COL_DESCRIPTION = "DESCRIPTION";
    public final static String COL_PUBDATE = "PUBDATE";



    public MyOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_LINK + " TEXT," +
            COL_DESCRIPTION + " TEXT," +
            COL_PUBDATE + " TEXT," +
            COL_TITLE + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create new table
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create new table
        onCreate(db);
    }

    public void delete(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Integer> database_ids = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT*FROM " + TABLE_NAME, null);

        while (c.moveToNext()) {
            database_ids.add(Integer.parseInt(c.getString(0)));
        }
        db.delete(TABLE_NAME, COL_ID + " =?", new String[]{String.valueOf(database_ids.get(orderId))});
    }

}
