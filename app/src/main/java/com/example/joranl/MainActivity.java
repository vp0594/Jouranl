package com.example.joranl;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);


        setUpTab();
    }

    private void setUpTab() {
        ArrayList<String> arrayList = new ArrayList<>(0);

        arrayList.add("List");
        arrayList.add("Calendar");

        tabLayout.setupWithViewPager(viewPager);
        prepareViewPager(viewPager, arrayList);
    }

    @Override
    protected void onResume() {
        super.onResume();
setUpTab();
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager(), getApplicationContext());

        adapter.addFragment(new ListFragment(), "  List");
        adapter.addFragment(new CalendarFragment(), "  Calendar");
        viewPager.setAdapter(adapter);
    }
}