package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Wolfgang on 04/04/2015.
 */
public class TaskInsertImage extends AsyncTask<MemoryImage, Void, MemoryImage> {

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
  protected MemoryImage doInBackground(MemoryImage... params) {
    MemoryDatabaseHandler mds = new MemoryDatabaseHandler(mContext);
    MemoryImage memoryImage = params[0];

    return mds.insertImage(memoryImage);
  }

  @Override
  protected void onPostExecute(MemoryImage memoryImage) {
    if(mMaterialProgressDialog.isShowing())
      mMaterialProgressDialog.dismiss();

    listener.onImageInserted(memoryImage);
  }

  public interface TaskInsertImageListener {
    public void onImageInserted(MemoryImage memoryImage);
  }
}