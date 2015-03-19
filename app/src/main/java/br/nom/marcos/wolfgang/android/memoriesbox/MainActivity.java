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
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.sql.SQLException;


public class MainActivity extends ActionBarActivity {

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
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
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

    Cursor memoryCursor = mds.getAllMemories();

    CursorAdapter cursorAdapter = new MemoriesListAdapter(this,
          memoryCursor, MemoriesListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

    memoryListView.setAdapter(cursorAdapter);

    Button button = (Button) findViewById(R.id.main_add_memory);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),MemoryDetailsViewer.class);
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

  private static final String TAG = "MainActivity";
  private MemoriesDataSource mds;
  private ListView memoryListView;

}
