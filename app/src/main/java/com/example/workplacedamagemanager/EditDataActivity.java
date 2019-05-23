package com.example.workplacedamagemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by User on 2/28/2017.
 */

public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete,btnImage;
    private EditText Ntxt, Dtxt, Datxt, Stxt;
    private ImageView Itxt;

   DatabaseHelper mDatabaseHelper;


    EditText editName, editDescription, editSeverity,editDate;
    Button add;
    private ListView mListView;

    private String selectedName;
    private int selectedID;
    private int selectedDate;
    private int selectedSeverity;
    private String selectedDescription;
    private byte[] selectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnImage = findViewById(R.id.button2);
        Ntxt = (EditText) findViewById(R.id.editable_item);
        Dtxt = (EditText) findViewById(R.id.editText_d);
        Itxt = (ImageView) findViewById(R.id.imageView);

        Datxt = (EditText) findViewById(R.id.editText_da);

        Stxt = (EditText) findViewById(R.id.editText_s);

        mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        selectedDescription = receivedIntent.getStringExtra("description");
        selectedImage = receivedIntent.getByteArrayExtra("image");

        selectedDate = receivedIntent.getIntExtra("date",-1);
        selectedSeverity = receivedIntent.getIntExtra("severity",-1);
        //set the text to show the current selected name
        Ntxt.setText(selectedName);
        Stxt.setText(Integer.toString(selectedSeverity));
        Datxt.setText(Integer.toString(selectedDate));
        Dtxt.setText(selectedDescription);

        Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImage,0,selectedImage.length);
        Itxt.setImageBitmap(bitmap);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String name = Ntxt.getText().toString();
            String description = Dtxt.getText().toString();
                int severity = Integer.parseInt(Stxt.getText().toString());
                int date = Integer.parseInt(Datxt.getText().toString());
                byte[] img = selectedImage;
                if(!name.equals("") && !description.equals("")){
                    mDatabaseHelper.updateName(name,selectedID,selectedName, description, date, severity,selectedImage);
                    Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(editScreenIntent);
                }else{
                    toastMessage("You must fill all fields");
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                Ntxt.setText("");
                toastMessage("removed from database");
                Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(editScreenIntent);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && (requestCode == 1)) {
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
            selectedImage = getBitmapAsByteArray(bitmap);

            ImageView Itxt = (ImageView) findViewById(R.id.imageView);
            bitmap = BitmapFactory.decodeByteArray(selectedImage,0,selectedImage.length);
            Itxt.setImageBitmap(bitmap);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}