package com.example.workplacedamagemanager;

import android.content.Intent;
import android.database.Cursor;
//import android.support.v7.app.AlertDialog; this is git
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText  search;
    Button add;
    Button file, refresh, se;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        search = (EditText) findViewById(R.id.editText);

        LayoutInflater factory = getLayoutInflater();
      View  view = factory.inflate(R.layout.record, null);
        mListView = (ListView)findViewById(R.id.listView);

        populateListView();
refresh = (Button)findViewById(R.id.refresh);
        se = (Button)findViewById(R.id.se);


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateListView();
            }
        });

        se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor data = myDb.getItemID(search.getText().toString());

                populateListViewSearch(data);
            }
        });




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }
    public void populateListViewSearch(Cursor data) {
        Log.d("hi", "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
    }
    public void populateListView() {
        Log.d("hi", "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myDb.getAllData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Cursor data = myDb.getItemID(name); //get the id associated with that name
                int itemID = -1;
                String description = "";
                int dateD =-1;
                int dateM= -1;
                int dateY=-1;
                int severity =-1 ;
                byte[] img=null;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                     description= data.getString(2);
                     dateM = data.getInt(3);
                    dateD = data.getInt(4);

                    dateY = data.getInt(5);

                    severity = data.getInt(6);
                    img = data.getBlob(7);


                }
                if(itemID > -1){
                    Intent editScreenIntent = new Intent(view.getContext(), EditDataActivity.class);
                    editScreenIntent.putExtra("id",itemID);
                    editScreenIntent.putExtra("name",name);
                    editScreenIntent.putExtra("description",description);

                    editScreenIntent.putExtra("datem",dateM);
                    editScreenIntent.putExtra("dated",dateD);
                    editScreenIntent.putExtra("datey",dateY);


                    editScreenIntent.putExtra("severity",severity);
                    editScreenIntent.putExtra("image",img);


                    startActivity(editScreenIntent);
                }
                else{
                    Toast.makeText(MainActivity.this,"NO ID ASSOCIATED",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filereport:
                Intent intent1 = new Intent(this, Statistics.class);
                this.startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }



}
