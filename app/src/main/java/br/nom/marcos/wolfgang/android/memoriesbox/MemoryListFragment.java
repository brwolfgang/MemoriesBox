package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
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

import com.afollestad.materialdialogs.MaterialDialog;

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
    View view = inflater.inflate(R.layout.fragment_memory_list, container, false);
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
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    getListView().setMultiChoiceModeListener(mMultiChoiceModeListener);

    new TaskRetrieveMemories(this).execute(new MemoryDatabaseHandler(getActivity().getApplicationContext()));

    initMemoriesAdapter();

    setListAdapter(memoryListAdapter);
  }

  private void initMemoriesAdapter() {
    String[] columnsFrom = {
        MemoryDatabaseHandler.MEMORY_COLUMN_TITLE,
        MemoryDatabaseHandler.MEMORY_COLUMN_DATE,
        MemoryDatabaseHandler.MEMORY_COLUMN_CONTENT};

    int[] viewsTo = {
        R.id.memories_list_item_title,
        R.id.memories_list_item_date,
        R.id.memories_list_item_content};

    memoryListAdapter = new MemoriesListAdapter(getActivity().getApplicationContext(), R.layout.memories_list_item,
        null, columnsFrom, viewsTo, MemoriesListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
  }

  private void deleteMemoryFromDatabase() {
    final TaskDeleteMemories.TaskDeleteMemoriesListener listener =
        (TaskDeleteMemories.TaskDeleteMemoriesListener) getActivity();

    //TODO export string resources
    new MaterialDialog.Builder(this.getActivity())
        .title("Delete selected memories?")
        .content("Deleted memories cannot be recovered.")
        .positiveText("Delete")
        .negativeText("Cancel")
        .callback(new MaterialDialog.ButtonCallback() {
          @Override
          public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            Log.i(TAG, "Memories to delete: " + getListView().getCheckedItemCount());
            new TaskDeleteMemories(getActivity(), listener)
                .execute(getListView().getCheckedItemIds());
          }
        })
        .show();
  }

  public interface MemoryListFragmentListener {
    public void editMemory(Long id);
    public void createNewMemory();
  }
}
