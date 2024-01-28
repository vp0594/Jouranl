    package com.example.joranl;

    import androidx.room.ColumnInfo;
    import androidx.room.Dao;
    import androidx.room.Delete;
    import androidx.room.Insert;
    import androidx.room.OnConflictStrategy;
    import androidx.room.Query;
    import androidx.room.Update;

    import java.util.List;

    @Dao
    public interface CalendarEntryDao {

        @Query("SELECT * FROM calendar_entries")
        List<CalendarEntry> getAllEntries();

        @Query("SELECT * FROM calendar_entries WHERE id = :entryId")
        CalendarEntry getEntryById(long entryId);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertEntry(CalendarEntry entry);

        @Update
        void updateEntry(CalendarEntry entry);

        @Delete
        void deleteEntry(CalendarEntry entry);

        @Query("SELECT * FROM calendar_entries WHERE entry_date BETWEEN :startDate AND :endDate")
        List<CalendarEntry> getEntriesForDateRange(String startDate, String endDate);

        @Query("SELECT * FROM calendar_entries WHERE entry_date LIKE :selectedMonth || '%'")
        List<CalendarEntry> getEntriesForMonth(String selectedMonth);

        @Query("SELECT id, entry_date, thumbnail_image_blob FROM calendar_entries")
        List<CalendarEntryWithThumbnail> getEntriesWithThumbnail();

        class CalendarEntryWithThumbnail {
            @ColumnInfo(name = "id")
            public long id;

            @ColumnInfo(name = "entry_date")
            public String entryDate;

            @ColumnInfo(name = "thumbnail_image_blob")
            public byte[] thumbnailImageBlob;
        }
    }
