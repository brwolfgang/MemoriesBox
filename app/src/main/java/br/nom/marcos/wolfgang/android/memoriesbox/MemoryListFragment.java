package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.sql.SQLException;

/**
 * Created by Wolfgang on 31/03/2015.
 */
public class MemoryListFragment extends ListFragment implements
    TaskRetrieveMemories.TaskRetrieveMemoriesListener {

  private static final String TAG = "MemoryListFragment";
  private AbsListView.MultiChoiceModeListener mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
    @Override
    public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
    }

    @Override
    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.memory_list_action_mode, menu);
      Log.i(TAG, "ActionMode created");
      return true;
    }

    @Override
    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
      return false;
    }

    @Override
    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
      switch (item.getItemId()) {
        case R.id.main_action_delete: {
          deleteMemoryFromDatabase();
          return true;
        }
        default:
          return false;
      }
    }

    @Override
    public void onDestroyActionMode(android.view.ActionMode mode) {
    }
  };
  private MemoryListFragmentListener listener;
  private MemoriesListAdapter memoryListAdapter;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      this.listener = (MemoryListFragmentListener) activity;
    } catch (ClassCastException e) {
      Log.i(TAG, getClass().getCanonicalName() + " must implement MemoryListFragmentListener");
    }
  }

  @Override
  public void onMemoriesRetrieved(Cursor cursor) {
    memoryListAdapter.changeCursor(cursor);
    getListView().deferNotifyDataSetChanged();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.memory_list_fragment, container, false);
    setHasOptionsMenu(true);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initResources();
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
      listener.editMemory(id);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    getActivity().getMenuInflater().inflate(R.menu.menu_memory_list, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.main_action_add_memory:
        listener.createNewMemory();
        return true;
    }
    return false;
  }

  private void initResources(){
    initDatabase();

    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    getListView().setMultiChoiceModeListener(mMultiChoiceModeListener);

    new TaskRetrieveMemories(this).execute(MemoriesDataSource.getInstance(getActivity().getApplicationContext()));

    initMemoriesAdapter();

    setListAdapter(memoryListAdapter);
  }

  private void initDatabase() {
    try {
      MemoriesDataSource.getInstance(getActivity().getApplicationContext()).open();
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getCause());
    }
  }

  private void initMemoriesAdapter() {
    String[] columnsFrom = {
        DatabaseUtil.COLUMN_TITLE,
        DatabaseUtil.COLUMN_DATE,
        DatabaseUtil.COLUMN_CONTENT};

    int[] viewsTo = {
        R.id.memories_list_item_title,
        R.id.memories_list_item_date,
        R.id.memories_list_item_content};

    memoryListAdapter = new MemoriesListAdapter(getActivity().getApplicationContext(), R.layout.memories_list_item,
        null, columnsFrom, viewsTo, MemoriesListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
  }

  private void deleteMemoryFromDatabase() {
    final TaskRetrieveMemories.TaskRetrieveMemoriesListener listener = this;
    new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        .setTitle("Delete selected memories?")
        .setMessage("Deleted memories cannot be recovered")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Log.i(TAG, "Memories to delete: " + getListView().getCheckedItemCount());
            new TaskDeleteMemories(getActivity(), listener)
                .execute(getListView().getCheckedItemIds());
          }
        })
        .setNegativeButton("No", null)
        .show();
  }

  public interface MemoryListFragmentListener {
    public void editMemory(Long id);
    public void createNewMemory();
  }
}
