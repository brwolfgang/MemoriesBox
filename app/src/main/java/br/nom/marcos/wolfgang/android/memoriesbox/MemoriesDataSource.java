package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Wolfgang Marcos on 17/03/2015.
 */
public class MemoriesDataSource {

  private static final String TAG = "MemoryDataSource";

  private static MemoriesDataSource mds;

  private SQLiteDatabase database;
  private DatabaseUtil dbUtil;
  private String[] allColumns = {DatabaseUtil.COLUMN_ID,
      DatabaseUtil.COLUMN_TITLE,
      DatabaseUtil.COLUMN_CONTENT,
      DatabaseUtil.COLUMN_DATE,
      DatabaseUtil.COLUMN_TIME};

  private MemoriesDataSource(Context context) {
    dbUtil = new DatabaseUtil(context);
  }

  public static MemoriesDataSource getInstance(Context context) {
    if (mds == null)
      mds = new MemoriesDataSource(context);

    return mds;
  }

  public void open() throws SQLException {
    if (database == null) {
      database = dbUtil.getWritableDatabase();
      Log.i(TAG, "Database was opened");
    }
  }

  public void close() {
    dbUtil.close();
    database = null;
  }

  public Memory createMemory(String title, String content, String date, String time) {
    ContentValues memoryContentValues = new ContentValues();
    memoryContentValues.put(DatabaseUtil.COLUMN_TITLE, title);
    memoryContentValues.put(DatabaseUtil.COLUMN_CONTENT, content);
    memoryContentValues.put(DatabaseUtil.COLUMN_DATE, date);
    memoryContentValues.put(DatabaseUtil.COLUMN_TIME, time);

    long insertID = database.insert(DatabaseUtil.TABLE_NAME, null, memoryContentValues);
    Memory createdMemory = retrieveMemory(insertID);

    Log.i(TAG, "Memory created id: " + createdMemory.getId());
    return createdMemory;
  }

  public Memory retrieveMemory(long memoryId) {
    Cursor cursor = database.query(DatabaseUtil.TABLE_NAME,
        allColumns, DatabaseUtil.COLUMN_ID + " = " + memoryId,
        null, null, null, null);

    cursor.moveToFirst();
    Memory retrievedMemory = cursorToMemory(cursor);

    cursor.close();

    Log.i(TAG, "Memory id: " + memoryId + " retrieved");
    return retrievedMemory;
  }

  public Cursor getAllMemories() {
    Cursor cursor = database.query(DatabaseUtil.TABLE_NAME, allColumns, null, null, null, null, null);
    cursor.moveToFirst();
    return cursor;
  }

  public void deleteMemory(Long memoryID) {
    database.delete(DatabaseUtil.TABLE_NAME, DatabaseUtil.COLUMN_ID + " = " + memoryID, null);
    Log.i(TAG, "Memory id " + memoryID + " deleted");
  }

  public Memory cursorToMemory(Cursor cursor) {
    Memory memory = new Memory();
    memory.setId(cursor.getLong(0));
    memory.setTitle(cursor.getString(1));
    memory.setContent(cursor.getString(2));
    memory.setDate(cursor.getString(3));
    memory.setTime(cursor.getString(4));

    Log.i(TAG, "Cursor to Memory performed id: " + memory.getId());
    return memory;
  }

  public Memory updateMemory(Memory memory) {
    ContentValues updateValues = new ContentValues();
    updateValues.put(DatabaseUtil.COLUMN_TITLE, memory.getTitle());
    updateValues.put(DatabaseUtil.COLUMN_CONTENT, memory.getContent());
    updateValues.put(DatabaseUtil.COLUMN_DATE, memory.getDate());
    updateValues.put(DatabaseUtil.COLUMN_TIME, memory.getTime());

    database.update(DatabaseUtil.TABLE_NAME, updateValues,
        DatabaseUtil.COLUMN_ID + " = " + memory.getId(), null);

    Log.i(TAG, "Memory id: " + memory.getId() + " updated");
    return retrieveMemory(memory.getId());
  }

  public void dropMemoriesTable() {
    try {
      open();
      database.execSQL("drop table " + DatabaseUtil.TABLE_NAME);
      close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
