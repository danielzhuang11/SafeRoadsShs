package com.example.workplacedamagemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.media.ExifInterface;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
public class Record extends AppCompatActivity {

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

    void showExif(Uri photoUri){
        if(photoUri != null){

            ParcelFileDescriptor parcelFileDescriptor = null;

            /*
            How to convert the Uri to FileDescriptor, refer to the example in the document:
            https://developer.android.com/guide/topics/providers/document-provider.html
             */
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                /*
                ExifInterface (FileDescriptor fileDescriptor) added in API level 24
                 */
                ExifInterface exifInterface = new ExifInterface(fileDescriptor);
                String exif="Exif: " + fileDescriptor.toString();

                exif += "\nGPS related:";
                exif += "\n TAG_GPS_DATESTAMP: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                exif += "\n TAG_GPS_TIMESTAMP: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                exif += "\n TAG_GPS_LATITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                exif += "\n TAG_GPS_LATITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                exif += "\n TAG_GPS_LONGITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                exif += "\n TAG_GPS_LONGITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                exif += "\n TAG_GPS_PROCESSING_METHOD: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

                parcelFileDescriptor.close();

                Toast.makeText(getApplicationContext(),
                        exif,
                        Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

            String strPhotoPath = photoUri.getPath();

        }else{
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && (requestCode == PICK_IMAGE)) {
//TODO: action
            Uri uri = data.getData();
            InputStream inputStream = null;
            showExif(uri);
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
            /*try {
                Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imgByte));
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        Log.d("PAY ATTENTION TO THIS ", tag.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ImageProcessingException e) {
                e.printStackTrace();
            }*/
                      ImageView Itxt = (ImageView) findViewById(R.id.imageView);
            bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            Itxt.setImageBitmap(bitmap);
            Itxt.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream);
        return outputStream.toByteArray();
    }

    public  void AddData()
    {
        if(!TextUtils.isEmpty(editName.getText())&& (!TextUtils.isEmpty(editSeverity.getText())&&TextUtils.isDigitsOnly(editSeverity.getText()))&& !TextUtils.isEmpty(editDateM.getText())&&TextUtils.isDigitsOnly(editDateM.getText())&& !TextUtils.isEmpty(editDateD.getText()) &&TextUtils.isDigitsOnly(editDateD.getText())&& !TextUtils.isEmpty(editDateY.getText())&&TextUtils.isDigitsOnly(editDateY.getText())&& !TextUtils.isEmpty(editDescription.getText())&& imgByte != null) {
        boolean isInserted = myDb.insertData(
                editName.getText().toString(),
                editDescription.getText().toString(),
                Integer.parseInt(editSeverity.getText().toString()),
                Integer.parseInt(editDateM.getText().toString()),Integer.parseInt(editDateD.getText().toString()),Integer.parseInt(editDateY.getText().toString()),imgByte);

        if (isInserted) {
            Toast.makeText(this, "Data inserted", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Data not inserted", Toast.LENGTH_LONG).show();
        }
        }else{
            Toast.makeText(this, "You must fill all fields", Toast.LENGTH_LONG).show();
        }


    }








}
