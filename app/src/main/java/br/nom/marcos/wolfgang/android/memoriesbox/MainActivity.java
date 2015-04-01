package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity implements
    MemoryListFragment.MemoryListFragmentListener, MemoryDetailsViewerFragment.MemoryDetailsViewerFragmentListener {

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

  }

  private void initResources() {
    fragmentManager = getFragmentManager();
    fragmentTransaction = fragmentManager.beginTransaction();
    memoryListFragment = new MemoryListFragment();
    fragmentTransaction.add(R.id.main_fragment_container, memoryListFragment);
    fragmentTransaction.commit();
  }
}