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
public class ImageCaptureController {
  private final static String TAG = "ImageCaptureController";
  private static ImageCaptureController mImageCaptureController;

  public static ImageCaptureController getInstance(){
    if (mImageCaptureController == null)
      mImageCaptureController = new ImageCaptureController();

    return mImageCaptureController;
  }

  public Intent getCameraIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File temporaryImageFile;
    try {
      temporaryImageFile = createTemporaryImageFile();
      takePictureIntent.putExtra(
          MediaStore.EXTRA_OUTPUT,
          Uri.fromFile(temporaryImageFile));
    } catch (IOException e) {
      Log.e(TAG, e.getCause().toString());
    }

    return takePictureIntent;
  }

  public Intent getGalleryIntent() {
    Intent actionGallery = new Intent();
    actionGallery.setType("image/*");
    actionGallery.setAction(Intent.ACTION_PICK);
    return actionGallery;
  }

  private File createTemporaryImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "MB_" + timeStamp + "_";

    File storageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES);

    Log.i(TAG, "Storage Dir " + storageDir.toString());

    File memoryBoxAlbum = new File(storageDir.getPath() + "/MemoryBox/");
    memoryBoxAlbum.mkdir();

    Log.i(TAG, "MemoryBox Dir " + memoryBoxAlbum.toString());

    File imageTempFile = File.createTempFile(
        imageFileName,  // File name prefix
        ".jpg",         // File name suffix
        memoryBoxAlbum  // Directory where the file is going to be saved
    );

    Log.i(TAG, "New Image path " + imageTempFile.toString());

    return imageTempFile;
  }
}
