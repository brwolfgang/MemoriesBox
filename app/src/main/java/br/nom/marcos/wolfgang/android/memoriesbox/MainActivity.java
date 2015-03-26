package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends ActionBarActivity implements
      MemoriesRetrieveTask.TaskConclusionListener {

  private static final String TAG = "MainActivity";
  private final int NEW_MEMORY = 000;
  private final int EDIT_MEMORY = 001;
  private ListView memoryListView;
  private MemoriesListAdapter memoriesAdapter;
  private Set<Long> batchSelectedMemories = Collections.synchronizedSet(new HashSet<Long>());
  private ActionMode mActionMode;
  private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
      MenuInflater inflater = actionMode.getMenuInflater();
      inflater.inflate(R.menu.menu_main_action_mode, menu);
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
          actionMode.finish();
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
      Log.i(TAG, "Action mode destroyed");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initResources();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case NEW_MEMORY:
        if (resultCode == RESULT_OK) {
          new MemoriesRetrieveTask(this).execute(MemoriesDataSource.getInstance(this));
          break;
        }
      case EDIT_MEMORY:
        if (resultCode == RESULT_OK) {
          new MemoriesRetrieveTask(this).execute(MemoriesDataSource.getInstance(this));
          break;
        }
    }
  }

  @Override
  protected void onDestroy() {
    MemoriesDataSource.getInstance(this).close();
    super.onDestroy();
  }

  @Override
  public void onPostExecute(Cursor cursor) {
    memoriesAdapter.changeCursor(cursor);
    memoriesAdapter.notifyDataSetChanged();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.main_action_add_memory:
        Intent intent = new Intent(getApplicationContext(), MemoryDetailsViewer.class);
        startActivityForResult(intent, NEW_MEMORY);
    }

    return super.onOptionsItemSelected(item);
  }

  private void initResources() {
    this.initDatabase();
    this.memoryListView = (ListView) findViewById(R.id.main_memory_list_view);
    this.memoryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    new MemoriesRetrieveTask(this).execute(MemoriesDataSource.getInstance(this));

    initMemoriesAdapter();

    memoryListView.setAdapter(memoriesAdapter);

    memoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode == null)
          editMemoryOnNewActivity(id);
      }
    });

    memoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode == null) {
          mActionMode = startSupportActionMode(mActionModeCallback);
          batchSelectedMemories.add(id);
          return true;
        } else {
          if (batchSelectedMemories.contains(id))
            batchSelectedMemories.remove(id);
          else
            batchSelectedMemories.add(id);
          return false;
        }
      }
    });
  }

  private void initDatabase() {
    try {
      MemoriesDataSource.getInstance(this).open();
      Log.i(TAG, "Database opened");
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

    memoriesAdapter = new MemoriesListAdapter(this, R.layout.memories_list_item,
          null, columnsFrom, viewsTo, MemoriesListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
  }

  private void editMemoryOnNewActivity(Long memoryID) {
    Intent intent = new Intent(getApplicationContext(), MemoryDetailsViewer.class);
    intent.putExtra("memoryID", memoryID);
    startActivityForResult(intent, EDIT_MEMORY);
  }

  private void deleteMemoryFromDatabase() {
    Log.i(TAG, "Memories to delete: " + batchSelectedMemories.size());
    Iterator<Long> iterator = batchSelectedMemories.iterator();
    while (iterator.hasNext()) {
      MemoriesDataSource.getInstance(getApplicationContext()).deleteMemory(iterator.next());
    }
    new MemoriesRetrieveTask(this).execute(MemoriesDataSource.getInstance(this));
  }
}
