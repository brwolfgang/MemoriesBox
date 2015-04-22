package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

public class TaskDeleteImages extends AsyncTask<long[], Void, Void> {

  private final String TAG = "TaskDeleteImages";
  private Context mContext;
  private MaterialDialog mMaterialProgressDialog;
  private TaskDeleteImagesListener listener;

  public TaskDeleteImages(Context mContext, TaskDeleteImagesListener listener) {
    this.mContext = mContext;
    this.listener = listener;
    mMaterialProgressDialog = new MaterialDialog.Builder(mContext)
        .content("Deleting selected images...")
        .build();
  }

  // TODO extract string resources
  @Override
  protected void onPreExecute() {
    mMaterialProgressDialog.show();
  }

  @Override
  protected Void doInBackground(long[]... params) {
    MemoryDatabaseHandler mds = new MemoryDatabaseHandler(mContext);

    Log.i(TAG, "Total images to be deleted: " + params[0].length);

    for(long id : params[0]) {
      MemoryImage image = mds.retrieveImage(id);
      File imageFile = new File(image.getImagePath());

      if (imageFile.delete())
        Log.i(TAG, "Image " + image.getImagePath() + " deleted");
      else
        Log.i(TAG, "Image " + image.getImagePath() + " NOT deleted");

      mds.deleteImage(id);
    }

    return null;
  }

  @Override
  protected void onPostExecute(Void avoid) {
    if(mMaterialProgressDialog.isShowing())
      mMaterialProgressDialog.dismiss();

    listener.onImagesDeleted();
  }

  public interface TaskDeleteImagesListener {
    public void onImagesDeleted();
  }
}
