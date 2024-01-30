package com.example.joranl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EntryActivity extends AppCompatActivity {


    private Button entryDatePicker;
    private DatePickerDialog datePickerDialog;
    private EditText entryEditTextAbove, entryEditTextBelow;
    private ImageView imageView;
    private ImageButton closeImageButton;
    private FloatingActionButton addImageFAB;
    private FloatingActionButton saveEntryFAB;
    private final int PERMISSION_REQUEST_MEDIA_IMAGES = 3;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 2;
    private Uri imageUri;
    private boolean hasPermission = false;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        entryDatePicker = findViewById(R.id.entryDatePicker);
        entryEditTextAbove = findViewById(R.id.entryEditTextAbove);
        entryEditTextBelow = findViewById(R.id.entryEditTextBelow);
        imageView = findViewById(R.id.imageView);
        closeImageButton = findViewById(R.id.closeImageButton);
        addImageFAB = findViewById(R.id.galleryFab);
        saveEntryFAB = findViewById(R.id.saveFab);

        //inti datePicker up to Current Date.
        initDatePicker();
        entryDatePicker.setText(getTodayDate());
        entryDatePicker.setOnClickListener(v -> datePickerDialog.show());

        //Focusing First EditText.
        entryEditTextAbove.requestFocus();

        addImageFAB.setOnClickListener(v -> getImage());

        closeImageButton.setOnClickListener(v -> closeImage());

        saveEntryFAB.setOnClickListener(v -> saveEntry());
    }

    private void saveEntry() {
        copyImage();
    }

    private void copyImage() {

        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            Toast.makeText(this, "yse", Toast.LENGTH_SHORT).show();
            String imagePath = getImagePath(imageUri);
            String imageName = getImageName(imageUri);

            try {
                saveImage(imagePath, imageName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveImage(String imagePath, String imageName) throws IOException {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = imageName.substring(imageName.lastIndexOf("."));
        String newName = "image_" + timeStamp + "." + fileExtension;

        Toast.makeText(this, newName, Toast.LENGTH_SHORT).show();
        FileOutputStream fos = openFileOutput(newName, MODE_APPEND);
        File file = new File(imagePath);

        byte[] bytes = getBytesFromFile(file);

        fos.write(bytes);
        fos.close();


//        byte[] byt = readBytesFromFile(newName);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        imageView.setImageBitmap(bitmap);
    }

    private byte[] readBytesFromFile(String name) throws IOException {
        FileInputStream fis = openFileInput(name);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        fis.close();
        return bos.toByteArray();
    }

    private byte[] getBytesFromFile(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    private String getImageName(Uri imageUri) {

        String result = null;

        if (imageUri.getScheme().equals("content")) {
            Cursor cursor = getApplicationContext().getContentResolver().query(imageUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
            cursor.close();
        }
        if (result == null) {
            result = imageUri.getPath();
            int c = result.lastIndexOf('/');
            if (c != -1) {
                result = result.substring(c + 1);
            }
        }
        return result;
    }

    private String getImagePath(Uri imageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(imageUri, projection, null, null, null);

        String path = null;

        if (cursor != null) {
            try {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(columnIndex);
                }
            } finally {
                cursor.close();
            }
        }

        return path;
    }


    private String getTodayDate() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = makeDateString(dayOfMonth, month, year);
            entryDatePicker.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1) {
            return "JAN";
        }
        if (month == 2) {
            return "FEB";
        }
        if (month == 3) {
            return "MAR";
        }
        if (month == 4) {
            return "APR";
        }
        if (month == 5) {
            return "MAY";
        }
        if (month == 6) {
            return "JUN";
        }
        if (month == 7) {
            return "JUL";
        }
        if (month == 8) {
            return "AUG";
        }
        if (month == 9) {
            return "SEP";
        }
        if (month == 10) {
            return "OCT";
        }
        if (month == 11) {
            return "NOV";
        }
        if (month == 12) {
            return "DEC";
        }

        return "JAN";
    }

    private void closeImage() {
        imageView.setImageDrawable(null);
        imageUri = null;
        closeImageButton.setVisibility(View.GONE);

        if (entryEditTextBelow.getText().toString().equals("")) {
            entryEditTextBelow.setVisibility(View.GONE);
            entryEditTextAbove.setVisibility(View.VISIBLE);
            entryEditTextAbove.requestFocus();
        } else {
            String temp;
            if (entryEditTextAbove.getText().toString().equals("")) {
                temp = entryEditTextBelow.getText().toString();
            } else {
                temp = entryEditTextAbove.getText().toString() + "\n\n" + entryEditTextBelow.getText().toString();
            }
            entryEditTextAbove.setVisibility(View.VISIBLE);
            entryEditTextAbove.setText(temp);
            entryEditTextBelow.setVisibility(View.GONE);
            entryEditTextAbove.requestFocus();
            entryEditTextAbove.setSelection(entryEditTextAbove.getText().length());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void getImage() {

        if (!hasPermission) {
            getImagePermission();
        }

    }

    private void pickImage() {
        Intent intentGallery = new Intent(Intent.ACTION_PICK);
        intentGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, GALLERY_REQUEST_CODE);
    }

    private void getImagePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            } else {
                pickImage();
            }
        }
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_MEDIA_IMAGES
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_MEDIA_IMAGES);
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
            pickImage();
        }
        if (requestCode == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
            pickImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {

                imageUri = data.getData();

                closeImageButton.setVisibility(View.VISIBLE);
                entryEditTextBelow.setVisibility(View.VISIBLE);

                if (entryEditTextAbove.getText().toString().equals("")) {
                    entryEditTextAbove.setVisibility(View.GONE);
                }

                entryEditTextBelow.requestFocus();
                imageView.setImageURI(data.getData());
            }
        }
    }
}