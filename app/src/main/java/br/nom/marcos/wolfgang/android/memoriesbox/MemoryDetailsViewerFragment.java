package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Wolfgang Marcos on 30/03/2015.
 */
public class MemoryDetailsViewerFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, TaskSaveMemory.TaskSaveMemoryListener{

  private static final String TAG = "MemoryDetailsViewer";
  private EditText memoryTitle;
  private EditText memoryContent;
  private TextView memoryDate;
  private TextView memoryTime;
  private Memory memory;
  private MemoryDetailsViewerFragmentListener listener;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_memory_viewer, container, false);
    setHasOptionsMenu(true);
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      listener = (MemoryDetailsViewerFragmentListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.getClass() + " must implement MemoryDetailsViewerFragmentListener");
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initResources();
  }

  @Override
  public void onResume() {
    super.onResume();
    if(getArguments() != null) {
      Long id = getArguments().getLong("memoryID");
      if(id == -1) {
        cleanMemoryDetailsViewerFragment();
      }else{
        memory = MemoriesDataSource.getInstance(getActivity().getApplicationContext())
            .retrieveMemory(getArguments().getLong("memoryID"));
        loadMemoryData();
      }
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_memory_details_viewer, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.details_viewer_action_save:
        saveMemory();
        break;
      case R.id.details_viewer_action_delete:
        deleteMemory();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
    memoryDate.setText(date);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    memoryTime.setText(getFormattedTime(hourOfDay, minute));
  }

  @Override
  public void onMemorySaved(Memory memory) {
    this.memory = memory;
    listener.onMemorySaved();
  }

  private void initResources() {
    Activity activity = getActivity();
    memoryTitle = (EditText) activity.findViewById(R.id.memory_viewer_edit_title);
    memoryContent = (EditText) activity.findViewById(R.id.memory_viewer_edit_content);
    memoryDate = (TextView) activity.findViewById(R.id.memory_viewer_date);
    memoryTime = (TextView) activity.findViewById(R.id.memory_viewer_hour);

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
  }

  private void loadMemoryData() {
    this.memoryTitle.setText(memory.getTitle());
    this.memoryContent.setText(memory.getContent());
    this.memoryDate.setText(memory.getDate());
    this.memoryTime.setText(memory.getTime());
    Log.i(TAG, "Memory ID " + memory.getId() + " data loaded");
  }

  private String getCurrentDate() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.DAY_OF_MONTH) + "/" +
        (calendar.get(Calendar.MONTH) + 1) + "/" +
        calendar.get(Calendar.YEAR);
  }

  private String getCurrentTime() {
    Calendar calendar = Calendar.getInstance();

    return getFormattedTime(
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE));
  }

  private String getFormattedTime(int hour, int minute) {
    String formattedTime;

    if (minute < 10)
      formattedTime = hour + ":0" + minute;
    else
      formattedTime = hour + ":" + minute;

    return formattedTime;
  }

  private void saveMemory() {
    String title = memoryTitle.getText().toString();
    String content = memoryContent.getText().toString();
    String date = memoryDate.getText().toString();
    String time = memoryTime.getText().toString();

    // TODO Remove this before publishing
    if (title.length() == 0) {
      title = "Example text";
      content = "Example content";
    }

    if (title.length() > 0 && content.length() > 0) {
      if (memory == null)
        memory = new Memory();

      memory.setTitle(title);
      memory.setContent(content);
      memory.setDate(date);
      memory.setTime(time);

      new TaskSaveMemory(getActivity(), this).execute(memory);
    }
  }

  public void cleanMemoryDetailsViewerFragment(){
    this.memory = null;
    this.memoryTitle.setText("");
    this.memoryContent.setText("");
    this.memoryDate.setText(getCurrentDate());
    this.memoryTime.setText(getCurrentTime());
    Log.i(TAG, "Cleaned up for new use");
  }

  //  TODO extract String resources to XML file
  private void deleteMemory() {
    if (memory == null) {
      listener.onMemoryDeleted();
      return;
    }

    new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        .setTitle("Delete memory?")
        .setMessage("A deleted memory cannot be recovered")
        .setNegativeButton("No", null)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            MemoriesDataSource.getInstance(getActivity().getApplicationContext()).deleteMemory(memory.getId());
            listener.onMemoryDeleted();
          }
        })
        .show();
  }

  public interface MemoryDetailsViewerFragmentListener {
    public void onMemorySaved();
    public void onMemoryDeleted();
  }
}