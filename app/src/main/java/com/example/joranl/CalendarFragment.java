package com.example.joranl;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Button previousMonth, nextMonth;
    List<CalendarEntry> monthEntry;

    public CalendarFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        monthYearText = view.findViewById(R.id.monthYearTV);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        previousMonth = view.findViewById(R.id.previousMonth);
        nextMonth = view.findViewById(R.id.nextMonth);
        selectedDate = LocalDate.now();


        getMonthEntry();

        previousMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.minusMonths(1);
            getMonthEntry();
        });

        nextMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.plusMonths(1);
            getMonthEntry();
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMonthEntry() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        new GetMonthEntryAsyncTask().execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {

        //Set Month & Year in TextView

        //Making arraylist for storing month's day. Used for display CalendarView.
        ArrayList<String> dayInMonth = dayInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(dayInMonth, this, monthEntry, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

    }

    private class GetMonthEntryAsyncTask extends AsyncTask<Void, Void, Void> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {
            AppDataBase db = Room.databaseBuilder(getContext(), AppDataBase.class, "CalendarEntry").build();
            String month = monthYearText.getText().toString();
            String[] monthYear = month.split(" ");
            monthEntry = db.calendarEntryDao().getMonthEntry(monthYear[0], monthYear[1]);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            setMonthView();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> dayInMonthArray(LocalDate date) {

        ArrayList<String> stringArrayList = new ArrayList<>();

        //Getting month from date for generating Day's of Month.
        YearMonth yearMonth = YearMonth.from(date);

        //Getting number of Day's of the month.
        int dayInMonth = yearMonth.lengthOfMonth();

        //Getting first date of month.
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 0; i < 42; i++) {
            int dayValue = i - dayOfWeek + 1;
            if (i < dayOfWeek || dayValue > dayInMonth) {
                stringArrayList.add("   ");
            } else {
                stringArrayList.add(String.valueOf(dayValue));
            }

        }

        return stringArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return date.format(formatter);
    }


    @Override
    public void onItemClick(int position, String dayText) {

    }
}