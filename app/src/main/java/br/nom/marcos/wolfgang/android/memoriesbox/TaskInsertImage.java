package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;

public class TaskInsertImage extends AsyncTask<MemoryImage, Void, Void> {

  private Context mContext;
  private MaterialDialog mMaterialProgressDialog;
  private TaskInsertImageListener listener;

  // TODO extract string resources
  public TaskInsertImage(Context mContext, TaskInsertImageListener listener) {
    this.mContext = mContext;
    this.listener = listener;
    this.mMaterialProgressDialog = new MaterialDialog.Builder(mContext)
        .progress(true, 100)
        .content("Inserting image...")
        .build();
  }

  @Override
  protected void onPreExecute() {
    mMaterialProgressDialog.show();
  }

  @Override
  protected Void doInBackground(MemoryImage... params) {
    MemoryDatabaseHandler mds = new MemoryDatabaseHandler(mContext);
    MemoryImage memoryImage = params[0];

    mds.insertImage(memoryImage);

    return null;
  }

  @Override
  protected void onPostExecute(Void avoid) {
    if(mMaterialProgressDialog.isShowing())
      mMaterialProgressDialog.dismiss();

    listener.onImageInserted();
  }

  public interface TaskInsertImageListener {
    public void onImageInserted();
  }
}