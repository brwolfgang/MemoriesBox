package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MemoryDetailsViewer extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_memory_viewer);

    initResources();

    Bundle extras = getIntent().getExtras();
    if(extras != null) {
      this.memory = MemoriesDataSource.getInstance(this).retrieveMemory((Long) extras.get("memoryID"));
      loadMemoryData();
    }
  }

  private void initResources() {
    memoryTitle = (EditText) findViewById(R.id.main_edit_title);
    memoryContent = (EditText) findViewById(R.id.main_edit_content);
    memoryButtonSave = (Button) findViewById(R.id.main_button_save);

    memoryButtonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String title = memoryTitle.getText().toString();
        String content = memoryContent.getText().toString();

        if (title.length() > 0 && content.length() > 0) {
          if (memory == null) {
            memory = MemoriesDataSource.getInstance(getApplicationContext()).createMemory(title, content, "HARDCODED");
            if (memory != null) {
              Toast.makeText(getApplicationContext(), "Memory created: " + memory.getTitle(), Toast.LENGTH_SHORT).show();
              setResult(Activity.RESULT_OK);
              finish();
            }
          }else{
            memory.setTitle(title);
            memory.setContent(content);
            memory = MemoriesDataSource.getInstance(getApplicationContext()).updateMemory(memory);

            Toast.makeText(getApplicationContext(), "Memory updated: " + memory.getTitle(), Toast.LENGTH_SHORT).show();

            setResult(Activity.RESULT_OK);
            finish();
          }
        }
      }
    });
    }

  private void loadMemoryData(){
    this.memoryTitle.setText(memory.getTitle());
    this.memoryContent.setText(memory.getContent());
  }

  private static final String TAG = "MemoryViewerActivity";
  private EditText memoryTitle;
  private EditText memoryContent;
  private Button memoryButtonSave;
  private Memory memory;
}
