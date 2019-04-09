package com.example.maptest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public MyViewHolder(ImageView v) {
            super(v);
            imageView = v;
        }
    }

    MyAdapter(ArrayList myDataset) {
        mDataset = myDataset;
    }

    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_image_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        setPic(holder.imageView, mDataset.get(position));
    }

    public int getItemCount() {
        return mDataset.size();
    }

    private void setPic(ImageView mImageView, String filePath) {
//        int targetW = Math.max(mImageView.getWidth(), 80);
//        int targetH = Math.max(mImageView.getHeight(), 80);
//
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, bmOptions);
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        int scaleFactor = Math.min(photoW/targetW, photoH/ targetH);
//        scaleFactor = Math.min(scaleFactor, 80);
//
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
    }
}
