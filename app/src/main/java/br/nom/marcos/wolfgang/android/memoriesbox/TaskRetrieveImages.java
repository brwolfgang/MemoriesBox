package br.nom.marcos.wolfgang.android.memoriesbox;

import android.os.AsyncTask;
import android.os.Bundle;

import java.util.LinkedList;

/**
 * Created by Wolfgang Marcos on 20/03/2015.
 */
public class TaskRetrieveImages extends AsyncTask<Bundle, Void, LinkedList<MemoryImage>> {

  TaskRetrieveImagesListener listener;

  public TaskRetrieveImages(TaskRetrieveImagesListener listener) {
    this.listener = listener;
  }

  @Override
  protected LinkedList<MemoryImage> doInBackground(Bundle... params) {
    LinkedList<MemoryImage> images;
    MemoryDatabaseHandler mds = (MemoryDatabaseHandler) params[0].get("mds");
    Long memoryID = (Long) params[0].get("memoryID");

    return mds.getImagesFromMemory(memoryID);
  }

  @Override
  protected void onPostExecute(LinkedList<MemoryImage> images) {
    this.listener.onImagesRetrieved(images);
  }

  public interface TaskRetrieveImagesListener {
    public void onImagesRetrieved(LinkedList<MemoryImage> images);
  }
}
