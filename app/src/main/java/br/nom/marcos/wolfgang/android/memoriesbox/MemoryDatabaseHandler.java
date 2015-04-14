package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Wolfgang Marcos on 17/03/2015.
 */
public class MemoryDatabaseHandler extends SQLiteOpenHelper{

  public static final String DATABASE_NAME = "memoryBox";
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
  private String[] allMemoryColumns = {MEMORY_COLUMN_ID,
      MEMORY_COLUMN_TITLE,
      MEMORY_COLUMN_CONTENT,
      MEMORY_COLUMN_DATE,
      MEMORY_COLUMN_TIME};

  public static final String IMAGE_TABLE_NAME =  "image";
  public static final String IMAGE_COLUMN_ID = "_id";
  public static final String IMAGE_COLUMN_MEMORY_ID = "memory_id";
  public static final String IMAGE_COLUMN_NAME = "image_path";
  private static final String CREATE_TABLE_IMAGES = "CREATE TABLE "
      + IMAGE_TABLE_NAME + "("
      + IMAGE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + IMAGE_COLUMN_MEMORY_ID + " INTEGER NOT NULL, "
      + IMAGE_COLUMN_NAME+ " TEXT NOT NULL);";
  private String[] allImageColumns = {IMAGE_COLUMN_ID,
      IMAGE_COLUMN_MEMORY_ID,
      IMAGE_COLUMN_NAME};

  private static final String TAG = "MemoryDatabaseHandler";
  private SQLiteDatabase database;

  public MemoryDatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    try {
      Log.i(TAG, "Creating database");
      db.execSQL(CREATE_TABLE_MEMORY);
      db.execSQL(CREATE_TABLE_IMAGES);
      Log.i(TAG, "Database created successfully");
    } catch (android.database.SQLException e) {
      Log.i(TAG, "Database creation failed");
      e.printStackTrace();
      System.err.println(e.getCause() == null ? "No cause was given" : e.getCause());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  public void open() {
    try {
      database = this.getWritableDatabase();
      Log.i(TAG, "Database was opened");
    } catch (Exception e) {
      e.getCause();
    }
  }

  public void close() {
    database.close();
    Log.i(TAG, "Database was closed");
  }

  public Memory createMemory(String title, String content, String date, String time) {
    ContentValues memoryContentValues = new ContentValues();
    memoryContentValues.put(MEMORY_COLUMN_TITLE, title);
    memoryContentValues.put(MEMORY_COLUMN_CONTENT, content);
    memoryContentValues.put(MEMORY_COLUMN_DATE, date);
    memoryContentValues.put(MEMORY_COLUMN_TIME, time);

    this.open();
    long insertID = database.insert(MEMORY_TABLE_NAME, null, memoryContentValues);
    Memory createdMemory = retrieveMemory(insertID);
    this.close();

    Log.i(TAG, "Memory created id: " + createdMemory.getId());
    return createdMemory;
  }

  public Memory retrieveMemory(long memoryId) {
    this.open();
    Cursor cursor = database.query(MEMORY_TABLE_NAME,
        allMemoryColumns, MEMORY_COLUMN_ID + " = " + memoryId,
        null, null, null, null);

    cursor.moveToFirst();
    Memory retrievedMemory = cursorToMemory(cursor);

    cursor.close();
    this.close();

    Log.i(TAG, "Memory id: " + memoryId + " retrieved");
    return retrievedMemory;
  }

  public Memory updateMemory(Memory memory) {
    ContentValues updateValues = new ContentValues();
    updateValues.put(MEMORY_COLUMN_TITLE, memory.getTitle());
    updateValues.put(MEMORY_COLUMN_CONTENT, memory.getContent());
    updateValues.put(MEMORY_COLUMN_DATE, memory.getDate());
    updateValues.put(MEMORY_COLUMN_TIME, memory.getTime());

    this.open();
    database.update(MEMORY_TABLE_NAME, updateValues,
        MEMORY_COLUMN_ID + " = " + memory.getId(), null);
    this.close();

    Log.i(TAG, "Memory id: " + memory.getId() + " updated");
    return retrieveMemory(memory.getId());
  }

  public void deleteMemory(Long memoryID) {
    this.open();
    database.delete(MEMORY_TABLE_NAME, MEMORY_COLUMN_ID + " = " + memoryID, null);
    Log.i(TAG, "Memory id " + memoryID + " deleted");
    this.close();
  }

  public Cursor getAllMemories() {
    this.open();
    Cursor cursor = database.query(MEMORY_TABLE_NAME, allMemoryColumns, null, null, null, null, null);
    cursor.moveToFirst();
    this.close();
    return cursor;
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

  public void dropMemoriesTable() {
      this.open();
      database.execSQL("drop table " + MEMORY_TABLE_NAME);
      this.close();
  }

  public void dropImagesTable() {
      this.open();
      database.execSQL("drop table " + IMAGE_TABLE_NAME);
      this.close();
  }
}
