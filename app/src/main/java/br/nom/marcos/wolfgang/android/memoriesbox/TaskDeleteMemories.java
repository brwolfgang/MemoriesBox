package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Wolfgang on 04/04/2015.
 */
public class TaskDeleteMemories extends AsyncTask<long[], Void, Void> {

  private Context mContext;
  private MaterialDialog mMaterialProgressDialog;
  private TaskDeleteMemoriesListener listener;
  public TaskDeleteMemories(Context mContext, TaskDeleteMemoriesListener listener) {
    this.mContext = mContext;
    this.listener = listener;
    mMaterialProgressDialog = new MaterialDialog.Builder(mContext)
        .content("Deleting memories...")
        .progress(true, 100).build();
  }

  // TODO extract string resources
  @Override
  protected void onPreExecute() {
    mMaterialProgressDialog.show();
  }

  @Override
  protected Void doInBackground(long[]... params) {
    MemoryDatabaseHandler mds = new MemoryDatabaseHandler(mContext);

    for(long id : params[0])
      mds.deleteMemory(id);

    return null;
  }

  @Override
  protected void onPostExecute(Void avoid) {
    if(mMaterialProgressDialog.isShowing())
      mMaterialProgressDialog.dismiss();

    listener.onMemoriesDeleted();
  }

  public interface TaskDeleteMemoriesListener {
    public void onMemoriesDeleted();
  }
}
