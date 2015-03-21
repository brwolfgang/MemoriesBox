package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mds.close();
    Log.i(TAG, "Database closed");
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
  }

  private void initResources(){
    this.initDatabase();
    this.memoryListView = (ListView) findViewById(R.id.main_memory_list_view);

    new MemoriesRetrieveTask(this).execute(mds);

    initMemoriesAdapter();

    memoryListView.setAdapter(memoriesAdapter);

    Button button = (Button) findViewById(R.id.main_add_memory);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MemoryDetailsViewer.class);
        startActivity(intent);
      }
    });
  }

  private void initDatabase(){
    try {
      mds = new MemoriesDataSource(this);
      mds.open();
      Log.i(TAG, "Database opened");
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getCause());
    }
  }

  private void initMemoriesAdapter(){
    String[] columnsFrom = {
        DatabaseUtil.COLUMN_TITLE,
        DatabaseUtil.COLUMN_CREATION_DATE,
        DatabaseUtil.COLUMN_CONTENT};

    int[] viewsTo = {
          R.id.memories_list_item_title,
          R.id.memories_list_item_date,
          R.id.memories_list_item_content};

    memoriesAdapter = new MemoriesListAdapter(this, R.layout.memories_list_item,
          null, columnsFrom, viewsTo, MemoriesListAdapter.NO_SELECTION);
  }

  private static final String TAG = "MainActivity";
  private MemoriesDataSource mds;
  private ListView memoryListView;
  private MemoriesListAdapter memoriesAdapter;

  @Override
  public void onPostExecute(Cursor cursor) {
    memoriesAdapter.changeCursor(cursor);
  }
}
