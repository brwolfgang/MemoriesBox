package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * An Adapter to list memories
 *
 * Created by Wolfgang Marcos on 18/03/2015.
 */
public class MemoriesListAdapter extends CursorAdapter {

  Context context;
  LayoutInflater inflater;

  public MemoriesListAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);

    this.context = context;
    this.inflater.from(context);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View newView = LayoutInflater.from(context).inflate(R.layout.memories_list_item, parent, false);

    ViewHolder holder = new ViewHolder();
    holder.title = (TextView) newView.findViewById(R.id.memories_list_item_title);
    holder.date = (TextView) newView.findViewById(R.id.memories_list_item_date);
    holder.content = (TextView) newView.findViewById(R.id.memories_list_item_content);

    holder.title.setText(cursor.getString(1));
    holder.date.setText(cursor.getString(3));
    holder.content.setText(cursor.getString(2));

    newView.setTag(holder);

    return newView;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    ViewHolder holder = (ViewHolder) view.getTag();

    holder.title.setText(cursor.getString(1));
    holder.date.setText(cursor.getString(3));
    holder.content.setText(cursor.getString(2));
  }

  private static class ViewHolder{
    TextView title;
    TextView date;
    TextView content;
  }
}
