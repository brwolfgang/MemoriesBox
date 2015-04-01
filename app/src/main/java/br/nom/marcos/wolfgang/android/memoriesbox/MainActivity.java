package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.DatePicker;
import android.widget.TimePicker;


public class MainActivity extends ActionBarActivity implements MemoryListFragment.MemoryListFragmentListener, MemoryDetailsViewerFragment.MemoryDetailsViewerFragmentListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

  private static final String TAG = "MainActivity";
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;
  private Fragment memoryListFragment;
  private Fragment memoryDetailsViewer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initResources();
  }

  @Override
  public void onMemorySaved() {

  }

  @Override
  public void editMemory(Long id) {

  }

  @Override
  public void createNewMemory() {
    fragmentTransaction = fragmentManager.beginTransaction();
    memoryDetailsViewer = new MemoryDetailsViewerFragment();
    fragmentTransaction.replace(R.id.main_fragment_container, memoryDetailsViewer);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    ((DatePickerDialog.OnDateSetListener) memoryDetailsViewer).onDateSet(view, year, monthOfYear, dayOfMonth);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    ((TimePickerDialog.OnTimeSetListener) memoryDetailsViewer).onTimeSet(view, hourOfDay, minute);
  }

  private void initResources() {
    fragmentManager = getFragmentManager();
    fragmentTransaction = fragmentManager.beginTransaction();
    memoryListFragment = new MemoryListFragment();
    fragmentTransaction.add(R.id.main_fragment_container, memoryListFragment);
    fragmentTransaction.commit();
  }
}