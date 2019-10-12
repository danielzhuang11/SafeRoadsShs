package com.example.workplacedamagemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
    EditText editName, editDescription, editSeverity,editDateM, editDateD, editDateY;
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
        editDateM = (EditText)findViewById(R.id.editText_dM);
        editDateD = (EditText)findViewById(R.id.editText_dD);
        editDateY = (EditText)findViewById(R.id.editText_dY);
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
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            Itxt.setImageBitmap(bitmap);
            Itxt.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void AddData()
    {
        if(!TextUtils.isEmpty(editName.getText())&& (!TextUtils.isEmpty(editSeverity.getText())&&TextUtils.isDigitsOnly(editSeverity.getText()))&& !TextUtils.isEmpty(editDateM.getText())&&TextUtils.isDigitsOnly(editDateM.getText())&& !TextUtils.isEmpty(editDateD.getText()) &&TextUtils.isDigitsOnly(editDateD.getText())&& !TextUtils.isEmpty(editDateY.getText())&&TextUtils.isDigitsOnly(editDateY.getText())&& !TextUtils.isEmpty(editDescription.getText())&& imgByte != null) {
        boolean isInserted = myDb.insertData(
                editName.getText().toString(),
                editDescription.getText().toString(),
                Integer.parseInt(editSeverity.getText().toString()),
                Integer.parseInt(editDateM.getText().toString()),Integer.parseInt(editDateD.getText().toString()),Integer.parseInt(editDateY.getText().toString()),imgByte);

        if (isInserted) {
            Toast.makeText(this, "Data inserted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Data not inserted", Toast.LENGTH_LONG).show();
        }
        }else{
            Toast.makeText(this, "You must fill all fields", Toast.LENGTH_LONG).show();
        }


    }








}
