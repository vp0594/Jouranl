package com.example.joranl;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface CalendarEntryDao {

    @Query("SELECT * FROM calendar_entries ORDER BY entryDateLong DESC")
    List<CalendarEntry> getAllEntriesOrderedByDate();


    @Query("SELECT * FROM calendar_entries WHERE id= :entryID")
    CalendarEntry getEntryById(long entryID);

    @Upsert
    void upsertEntry(CalendarEntry entry);

    @Delete
    void deleteEntry(CalendarEntry entry);


    @Query("SELECT * FROM calendar_entries WHERE entryDate LIKE :startPrefix || '%' || :endSuffix AND hasImage = 1")
    List<CalendarEntry> getMonthEntry(String startPrefix, String endSuffix);


}
