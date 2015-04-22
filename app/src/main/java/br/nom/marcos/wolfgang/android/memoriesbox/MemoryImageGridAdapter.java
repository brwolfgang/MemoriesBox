package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;

public class MemoryImageGridAdapter extends BaseAdapter {

  private static final String TAG = "MemoryImageGridAdapter";
  private Context context;
  private LinkedList<MemoryImage> images;

  public MemoryImageGridAdapter(Context context, LinkedList<MemoryImage> images) {
    this.context = context;
    this.images = images;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public int getCount() {
    return images.size();
  }

  @Override
  public Object getItem(int position) {
    return images.get(position);
  }

  @Override
  public long getItemId(int position) {
    return images.get(position).getImageID();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView;

    if (convertView == null){
      imageView = new ImageView(context);
    }else{
      imageView = (ImageView) convertView;
    }
    // TODO remove indicators before publishing
    Picasso.with(context).setIndicatorsEnabled(true);

    Picasso.with(context)
        .load(new File(images.get(position).getImagePath()))
        .resize(250,250)
        .centerCrop()
        .placeholder(R.drawable.loading)
        .error(R.drawable.error)
        .into(imageView);
    return imageView;
  }
}
