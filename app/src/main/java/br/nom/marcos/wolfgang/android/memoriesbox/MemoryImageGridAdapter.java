package br.nom.marcos.wolfgang.android.memoriesbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
    FrameLayout memoryImageFrame;
    ViewHolder holder;

    if (convertView != null){
      memoryImageFrame = (FrameLayout) convertView;
      holder = (ViewHolder) convertView.getTag();
    }else{
      memoryImageFrame = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.memory_image_item, null);
      holder = new ViewHolder();
      holder.mainImage = (ImageView) memoryImageFrame.findViewById(R.id.memory_image_item_image);
      holder.background = (ImageView) memoryImageFrame.findViewById(R.id.memory_image_item_background);
      holder.selectionIcon = (ImageView) memoryImageFrame.findViewById(R.id.memory_image_item_select_icon);
      memoryImageFrame.setTag(holder);
    }

    Picasso.with(context)
        .load(new File(images.get(position).getImagePath()))
        .resize(250,250)
        .centerCrop()
        .placeholder(R.drawable.loading)
        .error(R.drawable.error)
        .into(holder.mainImage);

    Picasso.with(context)
        .load(R.drawable.ic_action_accept)
        .into(holder.selectionIcon);

    return memoryImageFrame;
  }

  static class ViewHolder{
    ImageView mainImage;
    ImageView background;
    ImageView selectionIcon;
  }
}
