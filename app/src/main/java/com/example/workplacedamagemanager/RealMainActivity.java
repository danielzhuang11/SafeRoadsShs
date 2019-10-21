package com.example.workplacedamagemanager;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.view.View;

import android.widget.CompoundButton;


public class RealMainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    public static Boolean toggle = false;
    Switch switch1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setContentView(R.layout.main);
        switch1 = (Switch)findViewById(R.id.detect);
        switch1.setChecked(toggle);
        switch1.setOnCheckedChangeListener(this);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item2:
                Intent intent1 = new Intent(this, Record.class);
                this.startActivity(intent1);
                return true;
            case R.id.item4:
                Intent intent2 = new Intent(this, MainActivity.class);
                this.startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
        if(switch1.isChecked())
        {
            toggle=true;
            startService(new Intent(this, ShakeService.class));

        }else{
            toggle=false;
            stopService(new Intent(this, ShakeService.class));
        }
    }
    public void filereport(View view) {
        Intent intent1 = new Intent(this, Record.class);
        this.startActivity(intent1);
    }
    public void database(View view) {
        Intent intent1 = new Intent(this, MainActivity.class);
        this.startActivity(intent1);
    }
}
