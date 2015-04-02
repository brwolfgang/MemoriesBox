package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.DatePicker;
import android.widget.TimePicker;


public class MainActivity extends ActionBarActivity implements MemoryListFragment.MemoryListFragmentListener,
    MemoryDetailsViewerFragment.MemoryDetailsViewerFragmentListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener{

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
    loadMemoryListView();
  }

  @Override
  public void onMemoryDeleted() {
    loadMemoryListView();
  }

  @Override
  public void editMemory(Long id) {
    loadMemoryDetailsViewer(id);
  }

  @Override
  public void createNewMemory() {
    loadMemoryDetailsViewer(null);
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
    fragmentManager = getSupportFragmentManager();
    loadMemoryListView();
  }

  private void loadMemoryListView(){
    fragmentManager.popBackStackImmediate();
    fragmentTransaction = fragmentManager.beginTransaction();
    if (memoryListFragment == null)
      memoryListFragment = new MemoryListFragment();
    fragmentTransaction.replace(R.id.main_fragment_container, memoryListFragment);
    fragmentTransaction.commit();
  }

  private void loadMemoryDetailsViewer(Long id){
    fragmentTransaction = fragmentManager.beginTransaction();
    memoryDetailsViewer = new MemoryDetailsViewerFragment();
    if (id != null) {
      Bundle args = new Bundle();
      args.putLong("memoryID", id);
      memoryDetailsViewer.setArguments(args);
    }
    fragmentTransaction.replace(R.id.main_fragment_container, memoryDetailsViewer);
    fragmentTransaction.addToBackStack(memoryDetailsViewer.getClass().getName());
    fragmentTransaction.commit();
  }
}