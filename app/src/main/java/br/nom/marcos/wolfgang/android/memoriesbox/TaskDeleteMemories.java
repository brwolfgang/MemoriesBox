package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Wolfgang on 04/04/2015.
 */
public class TaskDeleteMemories extends AsyncTask<long[], Void, Void> {

  private Context mContext;
  private ProgressDialog mProgressDialog;
  private TaskRetrieveMemories.TaskConclusionListener listener;

  public TaskDeleteMemories(Context mContext, TaskRetrieveMemories.TaskConclusionListener listener) {
    this.mContext = mContext;
    this.listener = listener;
    this.mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
  }

  @Override
  protected void onPreExecute() {
    mProgressDialog.setMessage("Deleting memories...");
    mProgressDialog.show();
  }

  @Override
  protected Void doInBackground(long[]... params) {
    MemoriesDataSource mds = MemoriesDataSource.getInstance(mContext);

    for(long id : params[0])
      mds.deleteMemory(id);

    return null;
  }

  @Override
  protected void onPostExecute(Void avoid) {
    new TaskRetrieveMemories(listener).execute(MemoriesDataSource.getInstance(mContext));

    if(mProgressDialog.isShowing())
      mProgressDialog.dismiss();
  }
}
