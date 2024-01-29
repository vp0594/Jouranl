package com.example.joranl;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface CalendarEntryDao {

    @Query("SELECT * FROM calendar_entries")
    List<CalendarEntry> getAllEntrires();

    @Query("SELECT * FROM calendar_entries WHERE id= :entryID")
    CalendarEntry getEntryById(long entryID);

    @Upsert
    void upsertEntry(CalendarEntry entry);

    @Delete
    void deleteEntry(CalendarEntry entry);

    @Query("SELECT * FROM calendar_entries WHERE strftime('%Y-%m',entryDate) BETWEEN :startDate AND :endDate")
    List<CalendarEntry> getEnrieForMonth(String startDate, String endDate);


}
