package com.example.joranl;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class EntryActivity extends AppCompatActivity {


    private Button entryDatePicker;
    private DatePickerDialog datePickerDialog;
    private EditText entryEditTextAbove, entryEditTextBelow;
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