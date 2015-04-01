package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
public class MemoryDetailsViewerFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

  private static final String TAG = "MemoryDetailsViewerFragment";
  private EditText memoryTitle;
  private EditText memoryContent;
  private TextView memoryDate;
  private TextView memoryTime;
  private Memory memory;
  private MemoryDetailsViewerFragmentListener listener;
  private Context mContext;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_memory_viewer, container, false);
    if(getArguments() != null) {
      memory = (Memory) getArguments().get("memory");
      loadMemoryData();
    }
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mContext = activity.getApplicationContext();
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
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
    memoryDate.setText(date);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    memoryTime.setText(getFormattedTime(hourOfDay, minute));
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
  }

  private String getCurrentDate() {
    Calendar calendar = Calendar.getInstance();
    String currentDate =
        calendar.get(Calendar.DAY_OF_MONTH) + "/" +
        (calendar.get(Calendar.MONTH) + 1) + "/" +
        calendar.get(Calendar.YEAR);

    return currentDate;
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

  public interface MemoryDetailsViewerFragmentListener {
    public void onMemorySaved();
  }
}