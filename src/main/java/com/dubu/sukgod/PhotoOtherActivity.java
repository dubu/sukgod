package com.dubu.sukgod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.*;

import java.io.*;
import java.util.List;

/**
 * User: kingkingdubu
 * Date: 13. 9. 18
 * Time: 오후 2:01
 */
public class PhotoOtherActivity extends ListActivity {
    private static final int DELETE_ID = Menu.FIRST ;
    private static int RESULT_LOAD_IMAGE = 1;
    public Dialog progressDialog;
    private Activity activity;
    private List<ParseObject> todos;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_other);

        TextView empty = (TextView) findViewById(android.R.id.empty);
        empty.setVisibility(View.INVISIBLE);

        new RemoteDataTask().execute();
        registerForContextMenu(getListView());

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // 사진올리는거.
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete_photo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                // Delete the remote object
                final ParseObject todo = todos.get(info.position);

                new RemoteDataTask() {
                    protected Void doInBackground(Void... params) {
                        try {
                            todo.delete();
                        } catch (ParseException e) {
                        }
                        super.doInBackground();
                        return null;
                    }
                }.execute();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Uri uri = Uri.fromFile(new File(picturePath));
            try {
                //decodeUri(uri)    // bit map
                //InputStream iStream =   getContentResolver().openInputStream(uri);
                //byte[] inputData = getBytes(iStream);

                byte[] inputData = bitmapToByteArray(decodeUri(uri));
                savePhoto(inputData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public byte[] bitmapToByteArray(Bitmap $bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 300;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }

            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    private void savePhoto(final byte[] data) {
        new AsyncTask<Object, Object, String>() {
            List<ParseObject> photos;


            @Override
            protected void onPreExecute() {

                PhotoOtherActivity.this.progressDialog = ProgressDialog.show(PhotoOtherActivity.this, "",
                        "Upload...", true);

                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Object... params) {

                ParseObject parseObject;
                parseObject = new ParseObject("Other");
                ParseFile file = new ParseFile("photo.png", data);
                parseObject.put("img", file);

                parseObject.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        PhotoOtherActivity.this.progressDialog.dismiss();;
                        if (e == null) {
                            new RemoteDataTask().execute();
                        } else {
                            //myObjectSaveDidNotSucceed();
                        }
                    }
                });

                return null;

            }

            @Override
            protected void onPostExecute(String s) {

            }
        }.execute();
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of todos in sorted order
            ParseQuery query = new ParseQuery("Other");
            query.orderByDescending("_created_at");

            try {
                todos = query.find();
            } catch (ParseException e) {

            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            PhotoOtherActivity.this.progressDialog = ProgressDialog.show(PhotoOtherActivity.this, "",
                    "Loading...", true);

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.e("DUBULOG adapter", "adapper!!");
            PhotoOtherActivity.this.progressDialog.dismiss();
            setListAdapter(new OtherAdapter(todos, activity));

            TextView empty = (TextView) findViewById(android.R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }
    }

}

