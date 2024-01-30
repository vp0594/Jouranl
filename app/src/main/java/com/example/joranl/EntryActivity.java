package com.example.joranl;

import android.content.Intent;
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

public class EntryActivity extends AppCompatActivity {


    private DatePicker entryDatePicker;
    private EditText entryEditText;
    private ImageView imageView;
    private ImageButton closeImageButton;
    private FloatingActionButton addImageFAB;
    private FloatingActionButton saveEntryFAB;

    private final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        entryDatePicker = findViewById(R.id.entryDatePicker);
        entryEditText = findViewById(R.id.entryEditText);
        imageView = findViewById(R.id.imageView);
        closeImageButton = findViewById(R.id.closeImageButton);
        addImageFAB = findViewById(R.id.galleryFab);
        saveEntryFAB = findViewById(R.id.saveFab);

        addImageFAB.setOnClickListener(v -> getImage());
        closeImageButton.setOnClickListener(v -> {
            imageView.setImageDrawable(null);
            closeImageButton.setVisibility(View.GONE);
        });

    }

    private void getImage() {
        Intent intentGallery = new Intent(Intent.ACTION_PICK);
        intentGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
            closeImageButton.setVisibility(View.VISIBLE);
                imageView.setImageURI(data.getData());
            }
        }
    }
}