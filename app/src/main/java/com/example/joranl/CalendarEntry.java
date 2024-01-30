package com.example.joranl;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "calendar_entries")
public class CalendarEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "entryText")
    private String entryText;

    @ColumnInfo(name = "imgUri")
    private String imgUri;

    @ColumnInfo(name = "entryDate")
    private String entryDate;

    @ColumnInfo(name = "hasImage")
    private boolean hasImage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
