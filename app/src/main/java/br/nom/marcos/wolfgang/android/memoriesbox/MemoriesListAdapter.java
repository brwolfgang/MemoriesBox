package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Wolfgang Marcos on 18/03/2015.
 */
public class MemoriesListAdapter extends SimpleCursorAdapter {

  public MemoriesListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
    super(context, layout, c, from, to, flags);
  }
}
