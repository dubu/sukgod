package com.dubu.sukgod;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * User: kingkingdubu
 * Date: 13. 5. 30
 * Time: 오후 9:54
 */
public class PhotoViewActivity  extends Activity {

    private TextView nameText;
    private int position;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_photo);

        nameText = (TextView) findViewById(R.id.name);
        imageView= (ImageView) findViewById(R.id.imgUrl);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String cont = extras.getString("cont");
            String name = extras.getString("name");
            position = extras.getInt("position");

            if (name != null) {
                nameText.setText(cont);
                new DownloadImageTask(imageView)
                        .execute(name);

            }
        }

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("name", nameText.getText().toString());
                bundle.putInt("position", position);

                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
        }
    }

}
