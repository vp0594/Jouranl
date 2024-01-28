package com.example.joranl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static AppDatabase appDatabase;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "calendar_database").build();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ArrayList<String> arrayList = new ArrayList<>(0);

        arrayList.add("List");
        arrayList.add("Calendar");

        tabLayout.setupWithViewPager(viewPager);
        prepareViewPager(viewPager, arrayList);

    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager(), getApplicationContext());

        adapter.addFragment(new ListFragment(),"  List");
        adapter.addFragment(new CalendarFragment(),"  Calendar");
        viewPager.setAdapter(adapter);
    }
}