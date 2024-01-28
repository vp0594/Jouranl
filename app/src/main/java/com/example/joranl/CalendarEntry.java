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

    // Constructors...

    // Getter and setter for 'id'
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Getter and setter for 'text'
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter and setter for 'thumbnailImageBlob'
    public byte[] getThumbnailImageBlob() {
        return thumbnailImageBlob;
    }

    public void setThumbnailImageBlob(byte[] thumbnailImageBlob) {
        this.thumbnailImageBlob = thumbnailImageBlob;
    }

    // Getter and setter for 'imageUri'
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    // Getter and setter for 'entryDate'
    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }


}
