package com.example.workplacedamagemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Statistics extends AppCompatActivity {

public static final int PICK_IMAGE = 1;
    EditText editName, editDescription, editSeverity,editDate;
    Button add,img;
    DatabaseHelper myDb;
    byte[] imgByte = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.record);

        add = findViewById(R.id.button);
        editDate = (EditText)findViewById(R.id.editText_da);
        editDescription = (EditText)findViewById(R.id.editText_d);
        editName = (EditText)findViewById(R.id.editText_n);
        editSeverity = (EditText)findViewById(R.id.editText_s);
        img = findViewById(R.id.button2);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddData();
            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && (requestCode == PICK_IMAGE)) {
//TODO: action
            Uri uri = data.getData();
            InputStream inputStream = null;

            try {

                inputStream = getContentResolver().openInputStream(uri);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Bitmap bitmap;
            bitmap = BitmapFactory.decodeStream(inputStream);
            imgByte = getBitmapAsByteArray(bitmap);

            ImageView Itxt = (ImageView) findViewById(R.id.imageView);
            bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
            Itxt.setImageBitmap(bitmap);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void AddData()
    {
        boolean isInserted = myDb.insertData(
                editName.getText().toString(),
                editDescription.getText().toString(),
                Integer.parseInt(editSeverity.getText().toString()),
                Integer.parseInt(editDate.getText().toString()),imgByte);

        if (isInserted) {
            Toast.makeText(this, "Data inserted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Data not inserted", Toast.LENGTH_LONG).show();
        }

    }








}
