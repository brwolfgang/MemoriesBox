package br.nom.marcos.wolfgang.android.memoriesbox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;
import java.util.LinkedList;

public class MemoryDetailsViewerFragment extends Fragment implements
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener,
    TaskSaveMemory.TaskSaveMemoryListener,
    TaskRetrieveImages.TaskRetrieveImagesListener,
    TaskInsertImage.TaskInsertImageListener,
    TaskDeleteImages.TaskDeleteImagesListener{

  private static final String TAG = "MemoryDetailsViewer";
  private final int pickImageCameraCode = 0;
  private final int pickImageGalleryCode = 1;
  private EditText memoryTitle;
  private EditText memoryContent;
  private TextView memoryDate;
  private TextView memoryTime;
  private GridView memoryImageGrid;
  private Memory currentMemory;
  private Uri capturedImageURI;
  private ActionMode mActionMode;
  private AbsListView.MultiChoiceModeListener imageGridMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      MenuInflater inflater = mode.getMenuInflater();
      inflater.inflate(R.menu.memory_viewer_action_mode, menu);
      mActionMode = mode;
      Log.i(TAG, "ActionMode created");
      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      switch (item.getItemId()){
        case R.id.memory_viewer_action_mode_delete_image:
          deleteSelectedImages();
          return true;
      }
      return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
      mActionMode = null;
    }
  };

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_memory_viewer, container, false);
    setHasOptionsMenu(true);
    return view;
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
      if(id != -1) {
        currentMemory = new MemoryDatabaseHandler(getActivity().getApplicationContext())
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
      case R.id.details_viewer_action_add_image:
        // TODO Handle a case where a image is inserted before the memory is saved
        new MaterialDialog.Builder(getActivity())
            .items(R.array.memory_viewer_pick_image)
            .title(R.string.memory_viewer_action_add_image)
            .itemsCallback(new PickImageCallback())
            .show();
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i(TAG, "Result received!");
    if (resultCode == Activity.RESULT_OK)
      switch (requestCode){
        case pickImageCameraCode:
          Log.i(TAG, "Image from camera received");
          insertImageOnMemory(capturedImageURI);
          break;
        case pickImageGalleryCode:
          Log.i(TAG,"Image from gallery received");
          Uri importedImage = ImageCaptureUtil.importFile(getActivity(), data.getData());
          insertImageOnMemory(importedImage);
          break;
      }
    if (resultCode == Activity.RESULT_CANCELED) {
      Log.i(TAG, "User canceled image picking");
      if (requestCode == pickImageCameraCode)
        ImageCaptureUtil.deleteTempFile(getActivity(), capturedImageURI);
    }

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
    this.currentMemory = memory;
    Toast.makeText(getActivity(), "The memory was saved", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onImagesRetrieved(LinkedList<MemoryImage> images) {
    currentMemory.setImageList(images);
    loadMemoryImages();
  }

  @Override
  public void onImageInserted() {
    new TaskRetrieveImages(getActivity(), this).execute(currentMemory.getId());
  }

  @Override
  public void onImagesDeleted() {
    new TaskRetrieveImages(getActivity(), this).execute(currentMemory.getId());
  }

  private void initResources() {
    Activity activity = getActivity();
    memoryTitle = (EditText) activity.findViewById(R.id.memory_viewer_edit_title);
    memoryContent = (EditText) activity.findViewById(R.id.memory_viewer_edit_content);
    memoryDate = (TextView) activity.findViewById(R.id.memory_viewer_date);
    memoryTime = (TextView) activity.findViewById(R.id.memory_viewer_hour);
    memoryImageGrid = (GridView) activity.findViewById(R.id.memory_viewer_grid);

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

    memoryImageGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
    memoryImageGrid.setMultiChoiceModeListener(imageGridMultiChoiceModeListener);
  }

  private void loadMemoryData() {
    this.memoryTitle.setText(currentMemory.getTitle());
    this.memoryContent.setText(currentMemory.getContent());
    this.memoryDate.setText(currentMemory.getDate());
    this.memoryTime.setText(currentMemory.getTime());
    loadMemoryImages();

    Log.i(TAG, "Memory ID " + currentMemory.getId() + " data loaded");
  }

  private void loadMemoryImages(){
    MemoryImageGridAdapter mMemoryImageGridAdapter =
    		new MemoryImageGridAdapter(getActivity(), currentMemory.getImageList());
    this.memoryImageGrid.setAdapter(mMemoryImageGridAdapter);
    this.memoryImageGrid.invalidate();
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
    if (title.length() == 0)
      title = "No Title";
    if (content.length() == 0)
      content = "No Content";

    if (currentMemory == null)
      currentMemory = new Memory();

    currentMemory.setTitle(title);
    currentMemory.setContent(content);
    currentMemory.setDate(date);
    currentMemory.setTime(time);
    currentMemory.setImageList(new LinkedList<MemoryImage>());

    new TaskSaveMemory(getActivity(), this).execute(currentMemory);
  }

  private void deleteMemory() {
    final TaskDeleteMemories.TaskDeleteMemoriesListener listener =
        (TaskDeleteMemories.TaskDeleteMemoriesListener) getActivity();

    final long[] memoryID = new long[1];
    memoryID[0] = currentMemory != null ? currentMemory.getId() : 0;

    // TODO extract string resources
    new MaterialDialog.Builder(getActivity())
        .title("Delete memory?")
        .content("A deleted memory cannot be recovered.")
        .negativeText("Cancel")
        .positiveText("Delete")
        .callback(new MaterialDialog.ButtonCallback() {
          @Override
          public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            new TaskDeleteMemories(getActivity(), listener).execute(memoryID);
          }
        })
        .show();
  }

  private void deleteSelectedImages(){
    final TaskDeleteImages.TaskDeleteImagesListener listener = this;

    // TODO Extract string resources
    new MaterialDialog.Builder(getActivity())
        .title("Delete images?")
        .content("Deleted images cannot be recovered.")
        .negativeText("Cancel")
        .positiveText("Delete")
        .callback(new MaterialDialog.ButtonCallback() {
          @Override
          public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            new TaskDeleteImages(getActivity(), listener).execute(memoryImageGrid.getCheckedItemIds());
            mActionMode.finish();
          }
        })
        .show();
  }

  private void insertImageOnMemory(Uri imageURI){
    MemoryImage image = new MemoryImage();
    image.setMemoryID(currentMemory.getId());
    image.setImagePath(
        ImageCaptureUtil.getRealPathFromURI(getActivity(), imageURI)
    );

    new TaskInsertImage(getActivity(), this).execute(image);
  }

  public interface MemoryDetailsViewerFragmentListener {
    public void onMemorySaved();
  }

  private class PickImageCallback implements MaterialDialog.ListCallback{
    @Override
    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
      if (currentMemory == null)
        saveMemory();

      switch (i) {
        case 0:
          capturedImageURI = ImageCaptureUtil.createTemporaryImageFile(getActivity());
          if (capturedImageURI != null){
            startActivityForResult(
                ImageCaptureUtil.getCameraIntent(
                    capturedImageURI), pickImageCameraCode);
          }else{
            Toast.makeText(getActivity(), "There was an error handling this request", Toast.LENGTH_SHORT).show();
          }
          break;
        case 1:
          startActivityForResult(
              ImageCaptureUtil.getGalleryIntent(), pickImageGalleryCode);
          break;
        default:
          break;
      }
    }
  }
}