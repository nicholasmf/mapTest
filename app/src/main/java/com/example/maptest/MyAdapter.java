package com.example.maptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    private final ArrayList<String> emptyList = new ArrayList();

    MyAdapter(ArrayList myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.my_image_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachNow = false;

        View view = inflater.inflate(layoutId, parent, attachNow);
        MyViewHolder viewHolder = new MyViewHolder(view);

        Log.d("recycler view", "rv created");

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        setPic(holder.imgView, mDataset.get(position));
        Log.d("recycler view", mDataset.get(0));
    }

    @Override
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

        AsyncTask downloadImageTask = new MyAdapter.DownloadImageTask(mImageView).execute(filePath);
    }

    public void updateList(ArrayList newList) {
        mDataset = newList;
        this.notifyDataSetChanged();
    }

    public void clearList() {
        mDataset = emptyList;
        this.notifyDataSetChanged();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgView = (ImageView) itemView.findViewById(R.id.iv_view_holder__img);
        }
    }
}
