package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Wolfgang Marcos on 16/03/2015.
 */
public class DatabaseUtil extends SQLiteOpenHelper{

  public static final String MEMORY_TABLE_NAME = "memory";
  public static final String MEMORY_COLUMN_ID = "_id";
  public static final String MEMORY_COLUMN_TITLE = "title";
  public static final String MEMORY_COLUMN_CONTENT = "content";
  public static final String MEMORY_COLUMN_DATE = "date";
  public static final String MEMORY_COLUMN_TIME = "time";
  public static final String IMAGE_TABLE_NAME =  "image";
  public static final String IMAGE_COLUMN_ID = "_id";
  public static final String IMAGE_COLUMN_MEMORY_ID = "memory_id";
  public static final String IMAGE_COLUMN_NAME = "image_path";
  private static final String DATABASE_CREATE = "create table "
      + MEMORY_TABLE_NAME + "("
      + MEMORY_COLUMN_ID + " integer primary key autoincrement, "
      + MEMORY_COLUMN_TITLE + " text not null, "
      + MEMORY_COLUMN_CONTENT + " text not null, "
      + MEMORY_COLUMN_DATE + " text not null, "
      + MEMORY_COLUMN_TIME + " text not null); "
      + "create table "
      + IMAGE_TABLE_NAME + "("
      + IMAGE_COLUMN_ID + " integer primary key autoincrement, "
      + IMAGE_COLUMN_MEMORY_ID + " integer not null, "
      + IMAGE_COLUMN_NAME+ " text not null);";
  private static final String TAG = "DatabaseUtil";

  public DatabaseUtil(Context context) {
    super(context, MEMORY_TABLE_NAME, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      Log.i(TAG, "Going to execute: " + DATABASE_CREATE);
      db.execSQL(DATABASE_CREATE);
      Log.i(TAG, "Database " + MEMORY_TABLE_NAME + " created successfully");
    } catch (SQLException e) {
      Log.i(TAG, "Database " + MEMORY_TABLE_NAME + " creation failed");
      e.printStackTrace();
      System.err.println(e.getCause() == null ? "No cause was given" : e.getCause());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
