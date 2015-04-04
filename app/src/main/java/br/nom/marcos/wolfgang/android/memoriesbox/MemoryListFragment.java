package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Wolfgang on 31/03/2015.
 */
public class MemoryListFragment extends ListFragment implements
    MemoriesRetrieveTask.TaskConclusionListener{

  private static final String TAG = "MemoryListFragment";
  private MemoryListFragmentListener listener;
  private MemoriesListAdapter memoryListAdapter;
  private boolean batchSelectionMode = false;
  private Set<Long> batchSelectedMemories = Collections.synchronizedSet(new HashSet<Long>());
  private ActionMode mActionMode;
  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
      MenuInflater inflater = actionMode.getMenuInflater();
      inflater.inflate(R.menu.menu_main_action_mode, menu);
      Log.i(TAG, "ActionMode created");
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
      return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
      switch (menuItem.getItemId()) {
        case R.id.main_action_delete: {
          deleteMemoryFromDatabase();
          return true;
        }
        default:
          return false;
      }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
      mActionMode = null;
      batchSelectedMemories.clear();
      setBatchSelectionMode(false);
      Log.i(TAG, "Action mode destroyed");
    }
  };

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
    if (!batchSelectionMode)
      listener.editMemory(id);
    else {
      Log.i(TAG, "OnListItemClick foi chamado");
      handleItemListSelection(id);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.main_action_add_memory:
        listener.createNewMemory();
    }

    return true;
  }

  private void initResources(){
    initDatabase();

    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    new MemoriesRetrieveTask(this).execute(MemoriesDataSource.getInstance(getActivity().getApplicationContext()));

    initMemoriesAdapter();

    setListAdapter(memoryListAdapter);

    getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return handleItemListSelection(id);
      }
    });
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

  private void setBatchSelectionMode(boolean state) {
    this.batchSelectionMode = state;
  }

  private boolean handleItemListSelection(Long memoryID) {
    if (mActionMode == null) {
      setBatchSelectionMode(true);
      mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
      batchSelectedMemories.add(memoryID);
      Log.i(TAG, "Added to selection: " + memoryID);
      return true;
    } else {
      if (batchSelectedMemories.contains(memoryID)) {
        batchSelectedMemories.remove(memoryID);
        Log.i(TAG, "Removed from selection: " + memoryID);
        if (batchSelectedMemories.size() == 0)
          mActionMode.finish();
      } else {
        batchSelectedMemories.add(memoryID);
        Log.i(TAG, "Added to selection: " + memoryID);
      }
      return false;
    }
  }

  private void deleteMemoryFromDatabase() {
    final MemoriesRetrieveTask.TaskConclusionListener listener = this;
    new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        .setTitle("Delete selected memories?")
        .setMessage("Deleted memories cannot be recovered")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Log.i(TAG, "Memories to delete: " + batchSelectedMemories.size());
            Iterator<Long> iterator = batchSelectedMemories.iterator();
            while (iterator.hasNext()) {
              MemoriesDataSource.getInstance(getActivity().getApplicationContext()).deleteMemory(iterator.next());
            }
            batchSelectedMemories.clear();
            mActionMode.finish();
            new MemoriesRetrieveTask(listener)
                .execute(MemoriesDataSource.getInstance(getActivity().getApplicationContext()));
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
