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

  private static final String TAG = "DatabaseUtil";

  public static final String TABLE_NAME = "memories_table";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_CONTENT = "content";
  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_TIME = "time";

  private static final String DATABASE_CREATE = "create table "
        + TABLE_NAME + "("
        + COLUMN_ID + " integer primary key autoincrement, "
        + COLUMN_TITLE + " text not null, "
        + COLUMN_CONTENT + " text not null, "
        + COLUMN_DATE + " text not null, "
        + COLUMN_TIME + " text not null);";

  public DatabaseUtil(Context context) {
    super(context, TABLE_NAME, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      Log.i(TAG, "Going to execute: " + DATABASE_CREATE);
      db.execSQL(DATABASE_CREATE);
      Log.i(TAG, "Database " + TABLE_NAME + " created successfully");
    } catch (SQLException e) {
      Log.i(TAG, "Database " + TABLE_NAME + " creation failed");
      e.printStackTrace();
      System.err.println(e.getCause() == null ? "No cause was given" : e.getCause());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
