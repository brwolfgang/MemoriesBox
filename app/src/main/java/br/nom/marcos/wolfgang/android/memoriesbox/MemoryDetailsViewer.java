package br.nom.marcos.wolfgang.android.memoriesbox;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;


public class MemoryDetailsViewer extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_memory_viewer);

    initResources();

    Bundle extras = getIntent().getExtras();
    if(extras != null) {
      this.memory = (Memory) getIntent().getExtras().get("memory");
      loadMemoryData();
    }
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
  protected void onRestart() {
    super.onRestart();
    this.initDatabase();
  }

  private void initResources(){
    this.initDatabase();

    memoryTitle = (EditText) findViewById(R.id.main_edit_title);
    memoryContent = (EditText) findViewById(R.id.main_edit_content);
    memoryButtonSave = (Button) findViewById(R.id.main_button_save);

    memoryButtonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String title = memoryTitle.getText().toString();
        String content = memoryContent.getText().toString();

        if(title.length() > 0 && content.length() > 0){
          Memory tempMemory = mds.createMemory(title, content, "HARDCODED");
          Toast.makeText(getApplicationContext(), "Memory created: " + tempMemory.getTitle(), Toast.LENGTH_SHORT).show();
        }
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

  private void loadMemoryData(){
    this.memoryTitle.setText(memory.getTitle());
    this.memoryContent.setText(memory.getContent());
  }

  private static final String TAG = "MemoryViewerActivity";
  private EditText memoryTitle;
  private EditText memoryContent;
  private Button memoryButtonSave;
  private MemoriesDataSource mds;
  private Memory memory;
}
