package com.dubu.sukgod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.dubu.util.ONetworkInfo;
import com.parse.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * User: kingkingdubu
 * Date: 13. 5. 30
 * Time: 오후 8:47
 */
public class PhotoActivity extends ListActivity {
    private Activity activity;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_PHOTO = 2;

    private List<ParseObject> todos;
    public  Dialog progressDialog;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        // Override this method to do custom remote calls
        protected Void doInBackground(Void... params) {
            // Gets the current list of todos in sorted order
            ParseQuery query = new ParseQuery("Diary");
            query.orderByDescending("_created_at");

            try {
                todos = query.find();
            } catch (ParseException e) {

            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            PhotoActivity.this.progressDialog = ProgressDialog.show(PhotoActivity.this, "",
                    "Loading...", true);

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            /*
            // Put the list of todos into the list view
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(PhotoActivity.this,
                    R.layout.todo_row);
            for (ParseObject todo : todos) {
                DateFormat sdFormat = new SimpleDateFormat("MMdd");
                String tempDate = sdFormat.format(todo.getUpdatedAt());
                adapter.add(tempDate+(String) todo.get("name"));
            }
            setListAdapter(adapter);
            */

            Log.e("DUBULOG adapter","adapper!!");
            PhotoActivity.this.progressDialog.dismiss();
            setListAdapter(new PhotoAdapter(todos, activity));

            TextView empty = (TextView) findViewById(android.R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        TextView empty = (TextView) findViewById(android.R.id.empty);
        empty.setVisibility(View.INVISIBLE);

        new RemoteDataTask().execute();
        registerForContextMenu(getListView());

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        ParseObject todo = new ParseObject("Diary");
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
    }

    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, PhotoViewActivity.class);
        i.putExtra("name", todos.get(position).getString("name").toString());
        i.putExtra("position", position);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    */



}

/*

        extends Activity {

    private EditText nameText;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_todo);
        setTitle(R.string.create_todo);

        nameText = (EditText) findViewById(R.id.name);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            position = extras.getInt("position");

            if (name != null) {
                nameText.setText(name);
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


}
*/

