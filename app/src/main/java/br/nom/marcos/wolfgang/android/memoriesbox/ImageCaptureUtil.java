package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

  public static Uri createTemporaryImageFile(Context context){
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "MB_" + timeStamp;

    try {
      File imageTempFile = File.createTempFile(
          imageFileName,  // File name prefix
          ".jpg",         // File name suffix
          getMemoriesBoxAlbumFolder(context)  // Directory where the file is going to be saved
      );
      Log.i(TAG, "New Image path " + imageTempFile.toString());
      return Uri.fromFile(imageTempFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static File getMemoriesBoxAlbumFolder(Context context) {
    File storageDir = context.getExternalCacheDir();

    Log.i(TAG, "Storage Dir " + storageDir.toString());

    File memoriesBoxAlbum = new File(storageDir.getPath() + "/images/");
    memoriesBoxAlbum.mkdir();

    Log.i(TAG, "MemoryBox Dir " + memoriesBoxAlbum.toString());

    return memoriesBoxAlbum;
  }

  public static String getRealPathFromURI(Context context, Uri contentURI) {
    String result;
    Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
    if (cursor == null) {
      result = contentURI.getPath();
    } else {
      cursor.moveToFirst();
      int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      result = cursor.getString(idx);
      cursor.close();
    }
    return result;
  }

  public static void deleteTempFile(Context context, Uri fileURI){
    if (new File(getRealPathFromURI(context, fileURI)).delete())
      Log.i(TAG, "Temp file deleted");
    else
      Log.i(TAG, "Temp file WAS NOT deleted");
  }
}