package com.example.joranl;

import android.graphics.Bitmap;

public class EntryWithBItMap {
    private final CalendarEntry entry;
    private final Bitmap bitmap;

    public EntryWithBItMap(CalendarEntry entry, Bitmap bitmap) {
        this.entry = entry;
        this.bitmap = bitmap;
    }

    public CalendarEntry getEntry() {
        return entry;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
