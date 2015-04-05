package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Wolfgang on 04/04/2015.
 */
public class TaskSaveMemory extends AsyncTask<Memory, Void, Memory> {

  private Context mContext;
  private ProgressDialog mProgressDialog;
  private TaskSaveMemoryListener listener;
  public TaskSaveMemory(Context mContext, TaskSaveMemoryListener listener) {
    this.mContext = mContext;
    this.listener = listener;
    this.mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
  }

  @Override
  protected void onPreExecute() {
    mProgressDialog.setMessage(mContext.getString(R.string.memoy_save_progress_dialog));
    mProgressDialog.show();
  }

  @Override
  protected Memory doInBackground(Memory... params) {
    MemoriesDataSource mds = MemoriesDataSource.getInstance(mContext);
    Memory memory = params[0];

    if (memory.getId() == 0) {
      memory = mds.createMemory(
          memory.getTitle(),
          memory.getContent(),
          memory.getDate(),
          memory.getTime()
      );
    }else{
      memory = mds.updateMemory(memory);
    }
    return memory;
  }

  @Override
  protected void onPostExecute(Memory memory) {
    if(mProgressDialog.isShowing())
      mProgressDialog.dismiss();

    listener.onMemorySaved(memory);
  }

  public interface TaskSaveMemoryListener {
    public void onMemorySaved(Memory memory);
  }
}