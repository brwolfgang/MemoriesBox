package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MemoryDetailsViewer extends ActionBarActivity implements
      DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
    memoryDate.setText(date);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    String time = hourOfDay + ":" + minute;
    memoryTime.setText(time);
  }

  private void initResources() {
    memoryTitle = (EditText) findViewById(R.id.memory_viewer_edit_title);
    memoryContent = (EditText) findViewById(R.id.memory_viewer_edit_content);
    memoryDate = (TextView) findViewById(R.id.memory_viewer_date);
    memoryTime = (TextView) findViewById(R.id.memory_viewer_hour);
    memoryDate.setText(getCurrentDate());
    memoryTime.setText(getCurrentTime());

    memoryDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new DatePickerFragment().show(getFragmentManager(), "datepicker");
      }
    });

    memoryTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new TimePickerFragment().show(getFragmentManager(), "timepicker");
      }
    });

    memoryButtonSave = (Button) findViewById(R.id.memory_viewer_button_save);
    memoryButtonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String title = memoryTitle.getText().toString();
        String content = memoryContent.getText().toString();
        String date = memoryDate.getText().toString();
        String time = memoryTime.getText().toString();

        if (title.length() > 0 && content.length() > 0) {
          if (memory == null) {
            memory = MemoriesDataSource.getInstance(getApplicationContext()).createMemory(title, content, date, time);
            if (memory != null) {
              Toast.makeText(getApplicationContext(), "Memory created: " + memory.getTitle(), Toast.LENGTH_SHORT).show();
              setResult(Activity.RESULT_OK);
              finish();
            }
          }else{
            memory.setTitle(title);
            memory.setContent(content);
            memory.setDate(date);
            memory.setTime(time);
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
    this.memoryDate.setText(memory.getDate());
    this.memoryTime.setText(memory.getTime());
  }

  private String getCurrentDate(){
    Calendar calendar = Calendar.getInstance();
    String currentDate =
          calendar.get(Calendar.DAY_OF_MONTH) + "/" +
          (calendar.get(Calendar.MONTH) + 1) + "/" +
          calendar.get(Calendar.YEAR);

    return currentDate;
  }

  private String getCurrentTime(){
    Calendar calendar = Calendar.getInstance();
    String currentTime =
          calendar.get(Calendar.HOUR_OF_DAY) + ":" +
          calendar.get(Calendar.MINUTE);

    return currentTime;
  }

  private static final String TAG = "MemoryViewerActivity";
  private EditText memoryTitle;
  private EditText memoryContent;
  private Button memoryButtonSave;
  private TextView memoryDate;
  private TextView memoryTime;
  private Memory memory;
}
