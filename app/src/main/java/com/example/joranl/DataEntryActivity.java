package com.example.joranl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;

public class DataEntryActivity extends AppCompatActivity {

    private DatePicker dateSpinner;
    private EditText entryEditText;
    private FloatingActionButton addImageFab, saveEntryFab;
    private ImageView imageView;
    private ImageButton removeImageButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        dateSpinner = findViewById(R.id.dateSpinner);
        entryEditText = findViewById(R.id.entryEditText);
        addImageFab = findViewById(R.id.addImageFab);
        saveEntryFab = findViewById(R.id.saveEntryFab);
        imageView = findViewById(R.id.imageView);
        removeImageButton = findViewById(R.id.removeImageButton);

        addImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });


        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the image
                removeImage();
            }
        });

        // Set click listeners for FABs, handle image selection, and save entry logic here
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
