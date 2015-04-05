package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.DatePicker;
import android.widget.TimePicker;


public class MainActivity extends ActionBarActivity implements MemoryListFragment.MemoryListFragmentListener,
    MemoryDetailsViewerFragment.MemoryDetailsViewerFragmentListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, TaskDeleteMemories.TaskDeleteMemoriesListener{

  private static final String TAG = "MainActivity";
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;
  private MemoryListFragment memoryListFragment;
  private MemoryDetailsViewerFragment memoryDetailsViewer;

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
  public void editMemory(Long id) {
    loadMemoryDetailsViewer(id);
  }

  @Override
  public void onMemoriesDeleted() {
    loadMemoryListView();
  }

  @Override
  public void createNewMemory() {
    loadMemoryDetailsViewer(null);
  }

  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    memoryDetailsViewer.onDateSet(view, year, monthOfYear, dayOfMonth);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    memoryDetailsViewer.onTimeSet(view, hourOfDay, minute);
  }

  private void initResources() {
    fragmentManager = getSupportFragmentManager();
    memoryListFragment = new MemoryListFragment();
    memoryDetailsViewer = new MemoryDetailsViewerFragment();
    loadMemoryListView();
  }

  private void loadMemoryListView(){
    fragmentManager.popBackStackImmediate();
    fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.main_fragment_container, memoryListFragment);
    fragmentTransaction.commit();
  }

  private void loadMemoryDetailsViewer(Long id){
    fragmentTransaction = fragmentManager.beginTransaction();
    Bundle args = new Bundle();
    args.putLong("memoryID", id != null ? id : -1);
    memoryDetailsViewer.setArguments(args);
    fragmentTransaction.replace(R.id.main_fragment_container, memoryDetailsViewer);
    fragmentTransaction.addToBackStack(memoryDetailsViewer.getClass().getName());
    fragmentTransaction.commit();
  }
}