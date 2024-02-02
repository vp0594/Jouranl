package com.example.joranl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.Objects;

public class CalendarFragment extends Fragment {

    private TextView monthYearText, noOfEntries, noOfEntriesInCurrentMonth;
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
        noOfEntries = view.findViewById(R.id.noOfEntries);
        noOfEntriesInCurrentMonth = view.findViewById(R.id.noOfEntriesInCurrentMonth);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        previousMonth = view.findViewById(R.id.previousMonth);
        nextMonth = view.findViewById(R.id.nextMonth);
        selectedDate = LocalDate.now();

        getMonthEntry();
        setUpListeners();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMonthEntry() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        new GetMonthEntryAsyncTask().execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpListeners() {
        previousMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.minusMonths(1);
            getMonthEntry();
            animateRecyclerView(false);
        });

        nextMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.plusMonths(1);
            getMonthEntry();
            animateRecyclerView(true);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        ArrayList<String> dayInMonth = dayInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(dayInMonth, monthEntry, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private class GetMonthEntryAsyncTask extends AsyncTask<Void, Void, Void> {
        private int totalEntry;
        private int totalEntryInMonth;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {
            AppDataBase db = Room.databaseBuilder(getContext(), AppDataBase.class, "CalendarEntry").build();
            String month = monthYearText.getText().toString();
            String[] monthYear = month.split(" ");

            monthEntry = db.calendarEntryDao().getMonthEntry(monthYear[0], monthYear[1]);

            totalEntry = db.calendarEntryDao().getTotalEntryCount();
            totalEntryInMonth = db.calendarEntryDao().getCurrentMonthEntryCount(monthYear[0], monthYear[1]);

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            setMonthView();
            noOfEntries.setText(String.valueOf(totalEntry));
            noOfEntriesInCurrentMonth.setText(String.valueOf(totalEntryInMonth));
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

        if(Objects.equals(stringArrayList.get(0), "   ") && Objects.equals(stringArrayList.get(6), "   ") ){
            stringArrayList.subList(0,6).clear();
        }

        return stringArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return date.format(formatter);
    }

    private void animateRecyclerView(boolean isNext) {
        ObjectAnimator animator;
        if (isNext) {
            // Closing animation for the current month (slide to the left)
            animator = ObjectAnimator.ofFloat(calendarRecyclerView, "translationX", 0, -calendarRecyclerView.getWidth());
        } else {
            // Closing animation for the current month (slide to the right)
            animator = ObjectAnimator.ofFloat(calendarRecyclerView, "translationX", 0, calendarRecyclerView.getWidth());
        }
        animator.setDuration(700); // Set the duration of the closing animation

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Reset translationX after closing animation ends
                calendarRecyclerView.setTranslationX(0);

                // Perform the opening animation for the next/previous month
                ObjectAnimator openingAnimator;
                if (isNext) {
                    openingAnimator = ObjectAnimator.ofFloat(calendarRecyclerView, "translationX", calendarRecyclerView.getWidth(), 0);
                } else {
                    openingAnimator = ObjectAnimator.ofFloat(calendarRecyclerView, "translationX", -calendarRecyclerView.getWidth(), 0);
                }
                openingAnimator.setDuration(500); // Set the duration of the opening animation
                openingAnimator.start();
            }
        });

        animator.start();
    }

}