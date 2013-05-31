package com.dubu.sukgod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseObject;

import java.io.*;
import java.util.List;

/**
 * User: kingkingdubu
 * Date: 13. 5. 30
 * Time: 오후 9:16
 */
public class PhotoAdapter extends BaseAdapter {

    private List<ParseObject> itemlist;
    private Activity activity;

    private int columnCount = 4;

    public PhotoAdapter(List<ParseObject> itemlist, Activity activity) {
        this.itemlist = itemlist;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return itemlist.size()/columnCount;
    }

    @Override
    public Object getItem(int row) {
        return itemlist.get(row);
    }

    @Override
    public long getItemId(int position) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getView(int row, View convertView, ViewGroup parent) {
        Log.e("DUBULOG" , String.valueOf(row));
        if (convertView == null) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.itemview, parent, false);
        }

        ImageView imageView00 = (ImageView) convertView.findViewById(R.id.suk_imageView00);
        ImageView imageView01 = (ImageView) convertView.findViewById(R.id.suk_imageView01);
        ImageView imageView02 = (ImageView) convertView.findViewById(R.id.suk_imageView02);
        ImageView imageView03 = (ImageView) convertView.findViewById(R.id.suk_imageView03);

        if(itemlist.size() >row*columnCount+3  ) {
            final String col00 = (String) itemlist.get(row*columnCount).get("name");
            final String col01 = (String) itemlist.get(row*columnCount+1).get("name");
            final String col02 = (String) itemlist.get(row*columnCount+2).get("name");
            final String col03 = (String) itemlist.get(row*columnCount+3).get("name");

            final String cont00 = (String) itemlist.get(row*columnCount).get("cont");
            final String cont01 = (String) itemlist.get(row*columnCount+1).get("cont");
            final String cont02 = (String) itemlist.get(row*columnCount+2).get("cont");
            final String cont03 = (String) itemlist.get(row*columnCount+3).get("cont");

            new DownloadImageTask(imageView00).execute(col00.replace("image", "T150x150"));
            new DownloadImageTask(imageView01).execute(col01.replace("image","T150x150"));
            new DownloadImageTask(imageView02).execute(col02.replace("image","T150x150"));
            new DownloadImageTask(imageView03).execute(col03.replace("image","T150x150"));

            imageView00.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("name", col00);
                    i.putExtra("cont", cont00);
                    activity.startActivityForResult(i, 2);
                }
            });

            imageView01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("name", col01);
                    i.putExtra("cont", cont01);
                    activity.startActivityForResult(i, 2);
                }
            });

            imageView02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("name", col02);
                    i.putExtra("cont", cont02);
                    activity.startActivityForResult(i, 2);
                }
            });

            imageView03.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, PhotoViewActivity.class);
                    i.putExtra("name", col03);
                    i.putExtra("cont", cont03);
                    activity.startActivityForResult(i, 2);
                }
            });
        }
        return convertView;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {

           // ((PhotoActivity)activity).progressDialog = ProgressDialog.show(activity, "","Loading...", true);

            //super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            //super.onProgressUpdate(values);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            //((PhotoActivity)activity).progressDialog.dismiss();
        }
    }
}
