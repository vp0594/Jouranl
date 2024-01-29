package com.example.joranl;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.sql.Date;


@Entity(tableName = "calendar_entries")
public class CalendarEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "entryText")
    private String entryText;

    @ColumnInfo(name = "imgUri")
    private String imgUri;

    @ColumnInfo(name = "entryDate")
    private Date entryDate;

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

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
}
