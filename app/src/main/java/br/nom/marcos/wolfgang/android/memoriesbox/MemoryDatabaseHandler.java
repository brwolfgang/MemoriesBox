package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;

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
    if (database == null || !database.isOpen())
      try {
        database = this.getWritableDatabase();
        Log.i(TAG, "Database was opened");
      } catch (Exception e) {
        e.getCause();
      }
  }

  public void close() {
    if (database != null && database.isOpen()) {
      database.close();
      Log.i(TAG, "Database was closed");
    }
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

  public Memory retrieveMemory(long memoryID) {
    this.open();
    Cursor memoryCursor = database.query(MEMORY_TABLE_NAME,
        allMemoryColumns, MEMORY_COLUMN_ID + " = " + memoryID,
        null, null, null, null);

    memoryCursor.moveToFirst();
    Memory retrievedMemory = cursorToMemory(memoryCursor);
    Log.i(TAG, "Memory id: " + memoryID + " retrieved");

    memoryCursor.close();

    retrievedMemory.setImageList(getImagesFromMemory(retrievedMemory.getId()));

    this.close();

    return retrievedMemory;
  }

  public MemoryImage retrieveImage(long imageID){
    this.open();
    Cursor imageCursor = database.query(IMAGE_TABLE_NAME,
        allImageColumns, IMAGE_COLUMN_ID + " = " + imageID,
        null, null, null, null);

    LinkedList<MemoryImage> retrievedImagesList = cursorToImages(imageCursor);
    Log.i(TAG, "Total images retrieved: " + retrievedImagesList.size());
    MemoryImage retrievedImage = retrievedImagesList.get(0);
    imageCursor.close();
    this.close();
    return retrievedImage;
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

// TODO uncoment this before publishing
//    LinkedList<MemoryImage> images = getImagesFromMemory(memoryID);
//    for(MemoryImage image : images)
//      deleteImage(image.getImageID());

    database.delete(MEMORY_TABLE_NAME, MEMORY_COLUMN_ID + " = " + memoryID, null);
    Log.i(TAG, "Memory id " + memoryID + " deleted");
    database.delete(IMAGE_TABLE_NAME, IMAGE_COLUMN_MEMORY_ID + " = " + memoryID, null);
    Log.i(TAG, "Images from memory id " + memoryID + " deleted");
    this.close();
  }

  public void deleteImage(Long imageID){
    this.open();
    database.delete(IMAGE_TABLE_NAME, IMAGE_COLUMN_ID + " = " + imageID, null);
    Log.i(TAG, "Image id " + imageID + " deleted");
    this.close();
  }

  public Cursor getAllMemories() {
    this.open();
    Cursor cursor = database.query(MEMORY_TABLE_NAME, allMemoryColumns, null, null, null, null, null);
    Log.i(TAG, "All memories were retrieved");
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

  public MemoryImage insertImage(MemoryImage memoryImage){
    ContentValues imageContentValues = new ContentValues();
    imageContentValues.put(IMAGE_COLUMN_MEMORY_ID, memoryImage.getMemoryID());
    imageContentValues.put(IMAGE_COLUMN_NAME, memoryImage.getImagePath());

    this.open();
    Long imageID = database.insert(IMAGE_TABLE_NAME, null, imageContentValues);
    Log.i(TAG, "Image inserted with ID " + imageID);
    this.close();

    memoryImage.setImageID(imageID);

    return memoryImage;
  }

  public LinkedList<MemoryImage> getImagesFromMemory(Long memoryID){
    Cursor imageCursor = database.query(IMAGE_TABLE_NAME,
        allImageColumns, IMAGE_COLUMN_MEMORY_ID + " = " + memoryID,
        null, null, null, null);

    LinkedList<MemoryImage> memoryImages = cursorToImages(imageCursor);

    Log.i(TAG, "Total images loaded for memory " + memoryID + ": " + memoryImages.size());

    imageCursor.close();

    return memoryImages;
  }

  public LinkedList<MemoryImage> cursorToImages(Cursor cursor){
    LinkedList<MemoryImage> memoryImages= new LinkedList<MemoryImage>();

    if(cursor != null)
      while(cursor.moveToNext()) {
        memoryImages.add(new MemoryImage(
            cursor.getLong(0),
            cursor.getLong(1),
            cursor.getString(2)));
      }
    return memoryImages;
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
