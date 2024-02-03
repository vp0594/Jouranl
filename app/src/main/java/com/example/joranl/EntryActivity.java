package com.example.joranl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class EntryActivity extends AppCompatActivity {


    private Button entryDatePicker;
    private DatePickerDialog datePickerDialog;
    private EditText entryEditTextAbove, entryEditTextBelow;
    private ImageView imageView;
    private ImageButton closeImageButton, deleteImageButton;
    private FloatingActionButton addImageFAB;
    private FloatingActionButton saveEntryFAB;


    private final int PERMISSION_REQUEST_MEDIA_IMAGES = 3;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 2;
    private boolean hasPermission = false;

    private Uri imageUri;
    private String finalImageName = "";

    private Date dateOfEntry;
    private long dateOfEntryLong;

    private CalendarEntry entry;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        initUI();

        Intent intent = getIntent();
        int i = intent.getIntExtra("key", 0);

        if (i == 1) {

            deleteImageButton.setVisibility(View.VISIBLE);

            long id = intent.getLongExtra("id", -1);
            AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "CalendarEntry").allowMainThreadQueries().build();

            entry = db.calendarEntryDao().getEntryById(id);

            entryDatePicker.setText(entry.getEntryDate());
            if (entry.hasImage()) {
                byte[] bytes;
                try {
                    bytes = readBytesFromFile(entry.getImgUri());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Bitmap resizedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                closeImageButton.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(resizedBitmap);
            }

            if (entry.getEntryText().contains("@@@")) {
                String[] text = entry.getEntryText().split("@@@");

                if (text.length > 0 && !text[0].isEmpty()) {
                    entryEditTextAbove.setText(text[0]);
                }
                if (text.length > 1 && !text[1].isEmpty()) {
                    entryEditTextBelow.setVisibility(View.VISIBLE);
                    entryEditTextBelow.setText(text[1]);
                }

            } else {
                if (entry.getEntryText().startsWith("###")) {
                    String s = entry.getEntryText().replace("###", "");
                    entryEditTextBelow.setText(s);
                } else {
                    entryEditTextAbove.setText(entry.getEntryText());
                }
                if (entry.hasImage()) {
                    entryEditTextBelow.setVisibility(View.VISIBLE);
                    entryEditTextBelow.requestFocus();
                }
            }


        } else {

            entryDatePicker.setText(getTodayDate());

            //Focusing First EditText.
            entryEditTextAbove.requestFocus();
        }

        deleteImageButton.setOnClickListener(v -> deleteEntry());

        entryDatePicker.setOnClickListener(v -> openDatePicker());

        addImageFAB.setOnClickListener(v -> getImage());

        closeImageButton.setOnClickListener(v -> closeImage());

        saveEntryFAB.setOnClickListener(v -> {
            if (i == 1) {
                updateEntry();
            } else {
                saveEntry();
            }
        });
    }

    private void openDatePicker() {
        View dialogView = getLayoutInflater().inflate(R.layout.date_picker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        Button noButton = dialogView.findViewById(R.id.noButton);
        Button yesButton = dialogView.findViewById(R.id.yesButton);

        datePicker.setMaxDate(System.currentTimeMillis());

        AlertDialog dateDialog = builder.create();


        yesButton.setOnClickListener(v -> {

            int dayOfMonth = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            month = month + 1;
            int year = datePicker.getYear();

            String date = makeDateString(dayOfMonth, month, year);
            entryDatePicker.setText(date);
            dateOfEntry = new Date(year, month - 1, dayOfMonth);
            dateOfEntryLong = dateOfEntry.getTime();
            dateDialog.dismiss();
        });

        noButton.setOnClickListener(v -> dateDialog.dismiss());

        dateDialog.show();

    }

    private void initUI() {
        entryDatePicker = findViewById(R.id.entryDatePicker);
        entryEditTextAbove = findViewById(R.id.entryEditTextAbove);
        entryEditTextBelow = findViewById(R.id.entryEditTextBelow);
        imageView = findViewById(R.id.imageView);
        closeImageButton = findViewById(R.id.closeImageButton);
        deleteImageButton = findViewById(R.id.deleteImageButton);
        addImageFAB = findViewById(R.id.galleryFab);
        saveEntryFAB = findViewById(R.id.saveFab);
    }

    private void deleteEntry() {
        new DeleteEntryAsyncTask().execute();
    }

    private void updateEntry() {
        new UpdateEntryAsyncTask().execute();
    }

    private byte[] readBytesFromFile(String imgUri) throws IOException {
        FileInputStream fileInputStream = getApplicationContext().openFileInput(imgUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void saveEntry() {
        new SaveEntryAsyncTask().execute();
    }


    private void copyImage() {

        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
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
        String newName = "image_" + timeStamp + fileExtension;

        finalImageName = newName;

        FileOutputStream fos = openFileOutput(newName, MODE_APPEND);
        File file = new File(imagePath);

        byte[] bytes = getBytesFromFile(file);

        fos.write(bytes);
        fos.close();

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

        Date date = new Date(year, month - 1, day);
        dateOfEntryLong = date.getTime();

        return makeDateString(day, month, year);
    }

    private void closeImage() {
        imageView.setImageDrawable(null);
        imageUri = null;
        closeImageButton.setVisibility(View.GONE);

        String entryBelow = entryEditTextBelow.getText().toString();
        String entryAbove = entryEditTextAbove.getText().toString();

        entryEditTextAbove.setText(entryAbove + entryBelow);

        entryEditTextBelow.setText("");
        entryEditTextBelow.setVisibility(View.GONE);
    }


    private class UpdateEntryAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {


            String entryTextAbove;
            String entryTextBelow;
            String entryText = "";
            if (!entryEditTextAbove.getText().toString().equals("")) {
                entryTextAbove = entryEditTextAbove.getText().toString().trim();
                entryText = entryTextAbove + "@@@";
            }
            if (!entryEditTextBelow.getText().toString().equals("")) {
                entryTextBelow = entryEditTextBelow.getText().toString().trim();
                if (entryEditTextAbove.getText().toString().trim().equals("")) {
                    entryText = "###" + entryText + entryTextBelow;
                } else {
                    entryText = entryText + entryTextBelow;
                }
            }


            if (entryText.endsWith("@@@")) {
                entryText = entryText.substring(0, entryText.length() - 3);
            }

            entry.setEntryText(entryText);
            if (dateOfEntryLong == 0) {
                entry.setEntryDateLong(entry.getEntryDateLong());
            } else {
                entry.setEntryDateLong(dateOfEntryLong);
            }
            entry.setEntryDate(entryDatePicker.getText().toString());


            if(imageUri != null){
                copyImage();
                entry.setImgUri(finalImageName);
                entry.setHasImage(true);
            }

            if (imageView.getDrawable() == null){
                entry.setHasImage(false);
                entry.setImgUri("");
            }


            if (entryText.isEmpty() && entry.getImgUri().isEmpty()) {
                runOnUiThread(() -> Toast.makeText(EntryActivity.this, "Can not Save Empty Entry", Toast.LENGTH_SHORT).show());
                return null;
            }
            AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "CalendarEntry").build();

            db.calendarEntryDao().upsertEntry(entry);

            finish();

            return null;
        }
    }


    private class DeleteEntryAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "CalendarEntry").build();

            db.calendarEntryDao().deleteEntry(entry);

            finish();

            return null;
        }
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1) {
            return "Jan";
        }
        if (month == 2) {
            return "Feb";
        }
        if (month == 3) {
            return "Mar";
        }
        if (month == 4) {
            return "Apr";
        }
        if (month == 5) {
            return "May";
        }
        if (month == 6) {
            return "Jun";
        }
        if (month == 7) {
            return "Jul";
        }
        if (month == 8) {
            return "Aug";
        }
        if (month == 9) {
            return "Sept";
        }
        if (month == 10) {
            return "Oct";
        }
        if (month == 11) {
            return "Nov";
        }
        if (month == 12) {
            return "Dec";
        }

        return "Jan";
    }

    private class SaveEntryAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            copyImage();

            String entryTextAbove;
            String entryTextBelow;
            String entryText = "";
            if (!entryEditTextAbove.getText().toString().equals("")) {
                entryTextAbove = entryEditTextAbove.getText().toString().trim();
                entryText = entryTextAbove + "@@@";
            }
            if (!entryEditTextBelow.getText().toString().equals("")) {
                entryTextBelow = entryEditTextBelow.getText().toString().trim();
                if (entryEditTextAbove.getText().toString().trim().equals("")) {
                    entryText = "###" + entryText + entryTextBelow;
                } else {
                    entryText = entryText + entryTextBelow;
                }
            }


            if (entryText.endsWith("@@@")) {
                entryText = entryText.substring(0, entryText.length() - 3);
            }

            if (entryText.isEmpty() && finalImageName.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(EntryActivity.this, "Can not Save Empty Entry", Toast.LENGTH_SHORT).show());
                return null;
            }
            CalendarEntry calendarEntry = new CalendarEntry();
            calendarEntry.setEntryText(entryText);
            calendarEntry.setEntryDateLong(dateOfEntryLong);
            calendarEntry.setEntryDate(entryDatePicker.getText().toString());
            calendarEntry.setImgUri(finalImageName);
            if (!finalImageName.isEmpty()) {
                calendarEntry.setHasImage(true);
            }

            AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "CalendarEntry").build();

            db.calendarEntryDao().upsertEntry(calendarEntry);

            finish();

            return null;
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
            imageUri = null;
            if (requestCode == GALLERY_REQUEST_CODE) {

                if (data != null && data.getData() != null && isImageFile(data.getData())) {

                    imageUri = data.getData();

                    closeImageButton.setVisibility(View.VISIBLE);
                    entryEditTextBelow.setVisibility(View.VISIBLE);

                    if (entryEditTextAbove.getText().toString().equals("")) {
                        entryEditTextAbove.setVisibility(View.GONE);
                    }

                    entryEditTextBelow.requestFocus();
                    imageView.setImageURI(data.getData());
                }
                else {
                    // Handle the case where the selected content is not an image
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private boolean isImageFile(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("image");
    }
}