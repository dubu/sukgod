package com.dubu.sukgod;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

/**
 * User: kingkingdubu
 * Date: 13. 9. 18
 * Time: 오후 2:11
 */
public class OtherAdapter extends BaseAdapter {

    private static final int IN_SAMPLE_SIZE = 1;
    private List<ParseObject> itemlist;
    private Activity activity;
    private int columnCount = 1;

    public OtherAdapter(List<ParseObject> itemlist, Activity activity) {
        this.itemlist = itemlist;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int row) {
        return itemlist.get(row);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int row, View convertView, ViewGroup parent) {
        Log.e("DUBULOG", String.valueOf(row));
        if (convertView == null) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.otherview, parent, false);
        }
        final ImageView imageView00 = (ImageView) convertView.findViewById(R.id.suk_imageView00);

        // parse 데이터 가져오는것으로 대체..
        ParseFile applicantResume = (ParseFile) itemlist.get(row * columnCount).get("img");
        applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the resume
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = IN_SAMPLE_SIZE;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    imageView00.setImageBitmap(bitmap);
                } else {
                    // something went wrong
                }
            }
        });
        return convertView;
    }
}