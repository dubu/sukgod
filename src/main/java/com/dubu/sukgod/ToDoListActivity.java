package com.dubu.sukgod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.dubu.util.ONetworkInfo;
import com.parse.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class ToDoListActivity extends ListActivity {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_PHOTO = 2;

    public static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int PHOTO_ID = Menu.FIRST + 2;

    private MjpegView mv;
    private final String URL = "http://kozazz.iptime.org:8081/";

    private final String LED_URL = "http://kozazz.iptime.org/";

    private List<ParseObject> todos;
    private Dialog progressDialog;

    private Button btnWrite;
    private Button btnAlbum;
    private ToggleButton btnLight;




    private boolean isChecked ;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of todos in sorted order
            ParseQuery query = new ParseQuery("Todo");
            query.orderByDescending("_created_at");

            try {
                todos = query.find();
            } catch (ParseException e) {

            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            ToDoListActivity.this.progressDialog = ProgressDialog.show(ToDoListActivity.this, "",
                    "Loading...", true);

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            // Put the list of todos into the list view
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToDoListActivity.this,
                    R.layout.todo_row);
            for (ParseObject todo : todos) {
                DateFormat sdFormat = new SimpleDateFormat("MMdd");
                String tempDate = sdFormat.format(todo.getUpdatedAt());
                adapter.add(tempDate+(String) todo.get("name"));
            }
            setListAdapter(adapter);
            ToDoListActivity.this.progressDialog.dismiss();
            TextView empty = (TextView) findViewById(android.R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }
    }



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!ONetworkInfo.IsWifiAvailable(this) && !ONetworkInfo.Is3GAvailable(this))
        {
            Toast.makeText(this, "네크워크에 연결할 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }else if(!ONetworkInfo.IsWifiAvailable(this)){
            Toast.makeText(this, "대용량 데이터가 사용으로 wifi 사용을 권장합니다.", Toast.LENGTH_LONG).show();
        }

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, ToDoListActivity.class);
        PushService.subscribe(this, "Suk", ToDoListActivity.class);
        ParseAnalytics.trackAppOpened(getIntent());

        mv = (MjpegView) findViewById(R.id.mpeg_view);
        AsyncTask task = new AsyncTask<Object,Object,String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    mv.setSource(MjpegInputStream.read(URL));
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mv.showFps(false);


        TextView empty = (TextView) findViewById(android.R.id.empty);
        empty.setVisibility(View.INVISIBLE);

        new RemoteDataTask().execute();

        // check new photo shop

        new AsyncTask<Object,Object,String>() {
            ParseObject obj;
            @Override
            protected String doInBackground(Object... params) {

                ParseQuery query = new ParseQuery("Diary");
                query.orderByDescending("_updated_at");

                try {
                    obj = query.getFirst();

                } catch (ParseException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if(obj != null){

                    Date lastedDate = obj.getUpdatedAt();
                    Date now = new Date();

                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();

                    startCal.setTime(lastedDate);
                    endCal.setTime(now);

                    long diffMillis = endCal.getTimeInMillis() -  startCal.getTimeInMillis();
                    int diff =  (int)(diffMillis/(24*60*60*1000));
                    if(diff < 2){
                        btnAlbum.setText("N");
                    }
                    //ToDoListActivity.this.progressDialog.dismiss();
                }
            }
        }.execute();


        registerForContextMenu(getListView());

        new AsyncTask<Object,Object,String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(LED_URL+"/led/read");
                    HttpResponse response = null;
                    try {
                        response = client.execute(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response != null) {
                        HttpEntity entity = response.getEntity();

                        String rsVal =  EntityUtils.toString(entity);
                        if("on".equals(rsVal)){
                            isChecked = true;
                        }else{
                            isChecked = false;
                        }

                        if(isChecked){
                            btnLight.setChecked(true);
                            btnLight.setText(btnLight.getTextOn());
                        }else{
                            btnLight.setChecked(false);
                            btnLight.setText(btnLight.getTextOff());
                        }

                    }
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();

        btnWrite = (Button) findViewById(R.id.btn_write);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTodo();
            }
        });

        btnAlbum = (Button) findViewById(R.id.btn_album);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPhoto();
            }
        });

        btnLight = (ToggleButton )findViewById(R.id.btn_light);

        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnLight.isChecked()){
                    sendLightOn();
                }else{
                    sendLightOff();
                }
            }
        });



    }

    private String sendLightOn(){
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(LED_URL + "/led/on");
                    HttpResponse response = null;
                    try {
                        response = client.execute(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response != null) {
                        HttpEntity entity = response.getEntity();

                        String rsVal = EntityUtils.toString(entity);
                        return rsVal;
                    }
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
        return null;
    }

    private String sendLightOff(){
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(LED_URL + "/led/off");
                    HttpResponse response = null;
                    try {
                        response = client.execute(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response != null) {
                        HttpEntity entity = response.getEntity();

                        String rsVal = EntityUtils.toString(entity);
                        return rsVal;
                    }
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
        return null;
    }

    private void createTodo() {
        Intent i = new Intent(this, CreateTodo.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void createPhoto() {
        Intent i = new Intent(this, PhotoActivity.class);
        startActivityForResult(i, ACTIVITY_PHOTO);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mv.init(this);

        AsyncTask task = new AsyncTask<Object,Object,String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    mv.setSource(MjpegInputStream.read(URL));
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mv.showFps(false);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {
            return;
        }
        final Bundle extras = intent.getExtras();

        switch (requestCode) {
            case ACTIVITY_CREATE:
                new RemoteDataTask() {
                    protected Void doInBackground(Void... params) {
                        String name = extras.getString("name");
                        ParseObject todo = new ParseObject("Todo");
                        todo.put("name", name);
                        try {
                            todo.save();
                        } catch (ParseException e) {
                        }

                        super.doInBackground();
                        return null;
                    }
                }.execute();
                break;
            case ACTIVITY_EDIT:
                // Edit the remote object
                final ParseObject todo;
                todo = todos.get(extras.getInt("position"));
                todo.put("name", extras.getString("name"));

                new RemoteDataTask() {
                    protected Void doInBackground(Void... params) {
                        try {
                            todo.save();
                        } catch (ParseException e) {
                        }
                        super.doInBackground();
                        return null;
                    }
                }.execute();
                break;
        }



        mv.init(this);
        AsyncTask task = new AsyncTask<Object,Object,String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    mv.setSource(MjpegInputStream.read(URL));
                } catch (Exception e) {
                }
                return null;
            }
        }.execute();
        mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        mv.showFps(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, PHOTO_ID, 0, R.string.menu_photo);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createTodo();
                return true;
            case PHOTO_ID:
                createPhoto();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mv.init(this);
        Intent i = new Intent(this, CreateTodo.class);
        i.putExtra("name", todos.get(position).getString("name").toString());
        i.putExtra("position", position);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


}
