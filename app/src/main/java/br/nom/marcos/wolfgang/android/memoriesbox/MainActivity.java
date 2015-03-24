package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.sql.SQLException;


public class MainActivity extends ActionBarActivity implements MemoriesRetrieveTask.TaskConclusionListener{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initResources();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode){
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

  private void initResources(){
    this.initDatabase();
    this.memoryListView = (ListView) findViewById(R.id.main_memory_list_view);

    new MemoriesRetrieveTask(this).execute(MemoriesDataSource.getInstance(this));

    initMemoriesAdapter();

    memoryListView.setAdapter(memoriesAdapter);

    memoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), MemoryDetailsViewer.class);
        intent.putExtra("memoryID", id);
        startActivityForResult(intent, EDIT_MEMORY);
      }
    });

    Button button = (Button) findViewById(R.id.main_add_memory);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MemoryDetailsViewer.class);
        startActivityForResult(intent, NEW_MEMORY);
      }
    });
  }

  private void initDatabase(){
    try {
      MemoriesDataSource.getInstance(this).open();
      Log.i(TAG, "Database opened");
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getCause());
    }
  }

  private void initMemoriesAdapter(){
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

  private static final String TAG = "MainActivity";
  private ListView memoryListView;
  private MemoriesListAdapter memoriesAdapter;
  private final int NEW_MEMORY = 000;
  private final int EDIT_MEMORY = 001;
}
