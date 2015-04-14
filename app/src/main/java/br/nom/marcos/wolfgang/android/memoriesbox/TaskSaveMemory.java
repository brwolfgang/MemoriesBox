package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Wolfgang on 04/04/2015.
 */
public class TaskSaveMemory extends AsyncTask<Memory, Void, Memory> {

  private Context mContext;
  private MaterialDialog mMaterialProgressDialog;
  private TaskSaveMemoryListener listener;
  public TaskSaveMemory(Context mContext, TaskSaveMemoryListener listener) {
    this.mContext = mContext;
    this.listener = listener;
    this.mMaterialProgressDialog = new MaterialDialog.Builder(mContext)
        .progress(true, 100)
        .content("Saving memory...")
        .build();
  }

  @Override
  protected void onPreExecute() {
    mMaterialProgressDialog.show();
  }

  @Override
  protected Memory doInBackground(Memory... params) {
    MemoryDatabaseHandler mds = MemoryDatabaseHandler.getInstance(mContext);
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
    if(mMaterialProgressDialog.isShowing())
      mMaterialProgressDialog.dismiss();

    listener.onMemorySaved(memory);
  }

  public interface TaskSaveMemoryListener {
    public void onMemorySaved(Memory memory);
  }
}