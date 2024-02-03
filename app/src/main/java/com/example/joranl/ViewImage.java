package com.example.joranl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ViewImage extends AppCompatActivity {

    private CalendarEntry entry;
    private ImageView viewImage;
    private ImageButton closeViewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        viewImage = findViewById(R.id.viewImage);
        closeViewImage = findViewById(R.id.closeViewImage);

        Intent intent = getIntent();
        long id = intent.getLongExtra("id", 0);

        AppDataBase db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "CalendarEntry").allowMainThreadQueries().build();

        entry = db.calendarEntryDao().getEntryById(id);

        getImage();

        closeViewImage.setOnClickListener(v -> finish());
    }

    private void getImage() {
        byte[] bytes;
        try {
            bytes = readBytesFromFile(entry.getImgUri());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bitmap resizedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        viewImage.setImageBitmap(resizedBitmap);

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
}