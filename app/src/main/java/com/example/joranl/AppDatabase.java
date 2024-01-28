package com.example.joranl;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CalendarEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CalendarEntryDao calendarEntryDao();
}

