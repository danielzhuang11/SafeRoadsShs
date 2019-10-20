package com.example.workplacedamagemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import android.util.Base64;

/**
 * Created by User on 2/28/2017.
 */

public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete,btnImage,btnSend;
    private EditText Ntxt, Dtxt, DMtxt, DDtxt, DYtxt, Stxt;
    private ImageView Itxt;

   DatabaseHelper mDatabaseHelper;


    EditText editName, editDescription, editSeverity,editDateM, editDateD, editDateY;
    Button add;
    private ListView mListView;

    private String selectedName;
    private int selectedID;
    private int selectedDateM;
    private int selectedDateD;
    private int selectedDateY;
    private String selectedSeverity;
    private String selectedDescription;
    private byte[] selectedImage;

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnImage = findViewById(R.id.button2);
        btnSend = findViewById(R.id.btnSend);
        Ntxt = (EditText) findViewById(R.id.editable_item);
        Dtxt = (EditText) findViewById(R.id.editText_d);
        Itxt = (ImageView) findViewById(R.id.imageView);

        DMtxt = (EditText) findViewById(R.id.editText_dM);
        DDtxt = (EditText) findViewById(R.id.editText_dD);
        DYtxt = (EditText) findViewById(R.id.editText_dY);


        Stxt = (EditText) findViewById(R.id.editText_s);

        mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        selectedDescription = receivedIntent.getStringExtra("description");
        selectedDateM = receivedIntent.getIntExtra("datem",-1);
        selectedDateD = receivedIntent.getIntExtra("dated",-1);
        selectedDateY = receivedIntent.getIntExtra("datey",-1);
        selectedSeverity = receivedIntent.getStringExtra("severity");
        selectedImage = receivedIntent.getByteArrayExtra("image");
        //set the text to show the current selected name
        Ntxt.setText(selectedName);
        Stxt.setText((selectedSeverity));
        DMtxt.setText(Integer.toString(selectedDateM));
        DDtxt.setText(Integer.toString(selectedDateD));
        DYtxt.setText(Integer.toString(selectedDateY));
        Dtxt.setText(selectedDescription);

        Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImage,0,selectedImage.length);
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
        Itxt.setImageBitmap(bitmap);
        Itxt.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(Ntxt.getText())&& !TextUtils.isEmpty(Stxt.getText())&& !TextUtils.isEmpty(DMtxt.getText())&&!TextUtils.isEmpty(DDtxt.getText())&&!TextUtils.isEmpty(DYtxt.getText())&& !TextUtils.isEmpty(Dtxt.getText())&& hasImage(Itxt)){
                    String name = Ntxt.getText().toString();
            String description = Dtxt.getText().toString();

                String severity = (Stxt.getText().toString());
                int dateM = Integer.parseInt(DMtxt.getText().toString());
                int dateD = Integer.parseInt(DDtxt.getText().toString());
                int dateY = Integer.parseInt(DYtxt.getText().toString());
                byte[] img = selectedImage;
              //  if(!name.equals("") && !description.equals("") && img != null && !Integer.toString(severity).equals("") && !Integer.toString(date).equals("")){

                    mDatabaseHelper.updateName(name,selectedID,selectedName, description, dateM, dateD, dateY, severity,selectedImage);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToSheet();

            }
        });

    }
    private void addItemToSheet() {
        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
       final String name = Ntxt.getText().toString();
        final String description = Dtxt.getText().toString();
        final String severity = Stxt.getText().toString();
        final int dateM = Integer.parseInt(DMtxt.getText().toString());
        final  int dateD = Integer.parseInt(DDtxt.getText().toString());
        final  int dateY = Integer.parseInt(DYtxt.getText().toString());
        final byte[] img = selectedImage;
        final String encodedImage = Base64.encodeToString(img, Base64.DEFAULT);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzX0lhVQBZnfQURdQllg2RMlFMuBt2DRjUCq3Gp7QmlXsIvM1Ho/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(EditDataActivity.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","addItem");
                params.put("name",name);
                params.put("description",description);
                params.put("img",encodedImage);
                params.put("coords",severity);

                return params;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


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
            /*boolean supported = inputStream.markSupported();
            if(supported){
            inputStream.reset();
            }*/
            bitmap = BitmapFactory.decodeByteArray(selectedImage,0,selectedImage.length);
            Itxt.setImageBitmap(bitmap);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
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