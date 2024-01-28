package com.example.joranl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

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

        ArrayList<String> arrayList = new ArrayList<>(0);

        arrayList.add("List");
        arrayList.add("Calendar");

        tabLayout.setupWithViewPager(viewPager);
        prepareViewPager(viewPager, arrayList);

    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager(), getApplicationContext());

        ListFragment listFragment = new ListFragment();

        for (int i = 0; i < arrayList.size(); i++) {
            Bundle bundle = new Bundle();

            bundle.putString("title", arrayList.get(i));
            listFragment.setArguments(bundle);

            adapter.addFragment(listFragment, arrayList.get(i));

            listFragment = new ListFragment();
        }
        viewPager.setAdapter(adapter);
    }
}