package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wolfgang on 13/04/2015.
 */
public class ImageCaptureUtil {
  private final static String TAG = "ImageCaptureUtil";

  public static Intent getCameraIntent(Uri destinationFile) {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      takePictureIntent.putExtra(
          MediaStore.EXTRA_OUTPUT,
          destinationFile);
    return takePictureIntent;
  }

  public static Intent getGalleryIntent() {
    Intent actionGallery = new Intent();
    actionGallery.setType("image/*");
    actionGallery.setAction(Intent.ACTION_PICK);
    return actionGallery;
  }

  public static Uri createTemporaryImageFile(){
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "MB_" + timeStamp;

    File imageTempFile = null;
    try {
      imageTempFile = File.createTempFile(
          imageFileName,  // File name prefix
          ".jpg",         // File name suffix
          getMemoriesBoxAlbumFolder()  // Directory where the file is going to be saved
      );
      Log.i(TAG, "New Image path " + imageTempFile.toString());
      return Uri.fromFile(imageTempFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static File getMemoriesBoxAlbumFolder() {
    File storageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES);

    Log.i(TAG, "Storage Dir " + storageDir.toString());

    File memoriesBoxAlbum = new File(storageDir.getPath() + "/MemoryBox/");
    memoriesBoxAlbum.mkdir();

    Log.i(TAG, "MemoryBox Dir " + memoriesBoxAlbum.toString());

    return memoriesBoxAlbum;
  }
}