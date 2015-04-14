package br.nom.marcos.wolfgang.android.memoriesbox;

import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by Wolfgang Marcos on 20/03/2015.
 */
public class TaskRetrieveMemories extends AsyncTask<MemoryDatabaseHandler, Void, Cursor> {

  TaskRetrieveMemoriesListener listener;

  public TaskRetrieveMemories(TaskRetrieveMemoriesListener listener) {
    this.listener = listener;
  }

  @Override
  protected Cursor doInBackground(MemoryDatabaseHandler... params) {
    return params[0].getAllMemories();
  }

  @Override
  protected void onPostExecute(Cursor cursor) {
    super.onPostExecute(cursor);
    this.listener.onMemoriesRetrieved(cursor);
  }

  public interface TaskRetrieveMemoriesListener {
    public void onMemoriesRetrieved(Cursor cursor);
  }
}
