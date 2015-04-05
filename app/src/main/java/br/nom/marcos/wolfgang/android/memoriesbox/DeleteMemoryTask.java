package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Wolfgang on 04/04/2015.
 */
public class DeleteMemoryTask extends AsyncTask<long[], Void, Boolean> {

  private Context mContext;
  private ProgressDialog mProgressDialog;
  private MemoriesRetrieveTask.TaskConclusionListener listener;

  public DeleteMemoryTask(Context mContext, MemoriesRetrieveTask.TaskConclusionListener listener) {
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
  protected Boolean doInBackground(long[]... params) {
    MemoriesDataSource mds = MemoriesDataSource.getInstance(mContext);
    long[] memoryIDS = params[0];
    mProgressDialog.setMax(100);

    for(int i = 0; i < memoryIDS.length; i++) {
      mds.deleteMemory(memoryIDS[i]);
    }
    return true;
  }

  @Override
  protected void onPostExecute(Boolean aBoolean) {
    new MemoriesRetrieveTask(listener).execute(MemoriesDataSource.getInstance(mContext));

    if(mProgressDialog.isShowing())
      mProgressDialog.dismiss();
  }
}
