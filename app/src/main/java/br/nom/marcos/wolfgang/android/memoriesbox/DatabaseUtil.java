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
  private static final String CREATE_TABLE_MEMORY = "CREATE TABLE "
      + MEMORY_TABLE_NAME + "("
      + MEMORY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + MEMORY_COLUMN_TITLE + " TEXT NOT NULL, "
      + MEMORY_COLUMN_CONTENT + " TEXT NOT NULL, "
      + MEMORY_COLUMN_DATE + " TEXT NOT NULL, "
      + MEMORY_COLUMN_TIME + " TEXT NOT NULL);";
  public static final String IMAGE_TABLE_NAME =  "image";
  public static final String IMAGE_COLUMN_ID = "_id";
  public static final String IMAGE_COLUMN_MEMORY_ID = "memory_id";
  public static final String IMAGE_COLUMN_NAME = "image_path";
  private static final String CREATE_TABLE_IMAGES = "CREATE TABLE "
      + IMAGE_TABLE_NAME + "("
      + IMAGE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + IMAGE_COLUMN_MEMORY_ID + " INTEGER NOT NULL, "
      + IMAGE_COLUMN_NAME+ " TEXT NOT NULL);";
  private static final String TAG = "DatabaseUtil";



  public DatabaseUtil(Context context) {
    super(context, MEMORY_TABLE_NAME, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      Log.i(TAG, "Creating database");
      db.execSQL(CREATE_TABLE_MEMORY);
      db.execSQL(CREATE_TABLE_IMAGES);
      Log.i(TAG, "Database created successfully");
    } catch (SQLException e) {
      Log.i(TAG, "Database creation failed");
      e.printStackTrace();
      System.err.println(e.getCause() == null ? "No cause was given" : e.getCause());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
