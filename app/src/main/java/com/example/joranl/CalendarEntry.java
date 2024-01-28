package com.example.joranl;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calendar_entries")
public class CalendarEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "thumbnail_image_blob")
    private byte[] thumbnailImageBlob; // Small-sized image for quick access

    @ColumnInfo(name = "image_uri")
    private String imageUri; // URI to full-sized image

    @ColumnInfo(name = "entry_date")
    private String entryDate;

    // Constructors, getters, setters, etc.
}

