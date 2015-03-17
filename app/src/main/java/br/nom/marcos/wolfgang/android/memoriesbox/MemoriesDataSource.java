package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Wolfgang Marcos on 17/03/2015.
 */
public class MemoriesDataSource {
  private SQLiteDatabase database;
  private DatabaseUtil dbUtil;
  private String[] allColumns = {DatabaseUtil.COLUMN_ID,
      DatabaseUtil.COLUMN_TITLE,
      DatabaseUtil.COLUMN_CONTENT,
      DatabaseUtil.COLUMN_CREATION_DATE};

  public MemoriesDataSource(Context context) {
    dbUtil = new DatabaseUtil(context);
  }

  public void open() throws SQLException{
    database = dbUtil.getWritableDatabase();
  }

  public void close(){
    dbUtil.close();
  }

  public Memory createMemory(String title, String content, String creationDate){
    ContentValues memoryContentValues = new ContentValues();
    memoryContentValues.put(DatabaseUtil.COLUMN_TITLE, title);
    memoryContentValues.put(DatabaseUtil.COLUMN_CONTENT, content);
    memoryContentValues.put(DatabaseUtil.COLUMN_CREATION_DATE, creationDate);

    long insertID = database.insert(DatabaseUtil.TABLE_NAME, null, memoryContentValues);

    return retrieveMemory(insertID);
  }

  public Memory retrieveMemory(long memoryId){
    Cursor cursor = database.query(DatabaseUtil.TABLE_NAME,
          allColumns, DatabaseUtil.COLUMN_ID + " = " + memoryId,
          null,null,null,null);

    cursor.moveToFirst();
    Memory retrievedMemory = cursorToMemory(cursor);

    cursor.close();
    return retrievedMemory;
  }

  private Memory cursorToMemory(Cursor cursor){
    Memory memory = new Memory();
    memory.setId(cursor.getLong(0));
    memory.setTitle(cursor.getString(1));
    memory.setContent(cursor.getString(2));
    memory.setCreation_date(cursor.getString(3));

    return memory;
  }
}
