package com.example.joranl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DataEntryActivity extends AppCompatActivity {

    private DatePicker dateSpinner;
    private EditText entryEditText;
    private FloatingActionButton addImageFab, saveEntryFab;
    private ImageView imageView;
    private ImageButton removeImageButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        context=getApplicationContext();

        dateSpinner = findViewById(R.id.dateSpinner);
        entryEditText = findViewById(R.id.entryEditText);
        addImageFab = findViewById(R.id.addImageFab);
        saveEntryFab = findViewById(R.id.saveEntryFab);
        imageView = findViewById(R.id.imageView);
        removeImageButton = findViewById(R.id.removeImageButton);

        addImageFab.setOnClickListener(v -> openImagePicker());


        removeImageButton.setOnClickListener(v -> {
            // Remove the image
            removeImage();
        });

       saveEntryFab.setOnClickListener(v -> {
           // Get selected date
           int day = dateSpinner.getDayOfMonth();
           int month = dateSpinner.getMonth();
           int year = dateSpinner.getYear();

           Calendar calendar = Calendar.getInstance();
           calendar.set(year, month, day);

           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
           String entryDate = dateFormat.format(calendar.getTime());

           // Get entry text
           String entryText = entryEditText.getText().toString().trim();

           // If entry text is empty, add a placeholder
           if (entryText.isEmpty()) {
               entryText = "";
           }

           // If image is selected, save it with a proper name
           String imageName = "";
           if (selectedImageUri != null) {
               imageName = saveImage(selectedImageUri);
           }
           Toast.makeText(context, imageName  , Toast.LENGTH_SHORT).show();
       });
    }

    private String saveImage(Uri selectedImageUri) {
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        File internalStorageDir = context.getFilesDir();
        File imageFile = new File(internalStorageDir, imageName);

        try {
            InputStream imageStream = context.getContentResolver().openInputStream(selectedImageUri);
            if (imageStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                try (FileOutputStream out = new FileOutputStream(imageFile)) {
                    // Compress the bitmap to JPEG format
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }

                // Release resources
                imageStream.close();
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageName;
    }


    private void removeImage() {
        imageView.setImageDrawable(null);
        removeImageButton.setVisibility(View.GONE);

        // Reset the selected image URI
        selectedImageUri = null;
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                // Load the selected image into the ImageView
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(selectedImageBitmap);

                // Show the "X" button
                removeImageButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
