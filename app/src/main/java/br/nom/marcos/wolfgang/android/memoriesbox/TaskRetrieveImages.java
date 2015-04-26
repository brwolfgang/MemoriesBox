package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.os.AsyncTask;

import java.util.LinkedList;

public class TaskRetrieveImages extends AsyncTask<Long, Void, LinkedList<MemoryImage>> {

  private TaskRetrieveImagesListener listener;
  private Context mContext;

  public TaskRetrieveImages(Context context, TaskRetrieveImagesListener listener) {
    this.mContext = context;
    this.listener = listener;
  }

  @Override
  protected LinkedList<MemoryImage> doInBackground(Long... params) {
    MemoryDatabaseHandler mds = new MemoryDatabaseHandler(mContext);
    Long memoryID = params[0];

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
