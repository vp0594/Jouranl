package com.example.joranl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

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

        tabLayout.setupWithViewPager(viewPager);
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager(), getApplicationContext());

        adapter.addFragment(new ListFragment(), "  List");
        adapter.addFragment(new CalendarFragment(), "  Calendar");
        viewPager.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int from = sharedPreferences.getInt("from", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (from == 1) {
            editor.putInt("from", 0);
            editor.apply();
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpTab();


    }

}