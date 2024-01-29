package com.example.joranl;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CalendarEntry.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CalendarEntryDao calendarEntryDao();
}
