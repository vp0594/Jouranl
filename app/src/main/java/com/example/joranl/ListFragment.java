package com.example.joranl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class ListFragment extends Fragment {
    RecyclerView entryView;

    private FloatingActionButton addEntryFAB;
    private List<CalendarEntry> allEntries;


    public ListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        addEntryFAB = view.findViewById(R.id.addEntryFAB);
        entryView = view.findViewById(R.id.entryView);

        getEntries();

        addEntryFAB.setOnClickListener(v -> startActivity(new Intent(getActivity(), EntryActivity.class)));


        return view;
    }

    private void getEntries() {
        new GetAllEntriesAsyncTask().execute();
    }

    public void setEntriesView() {

        EntyViewAdapter entyViewAdapter = new EntyViewAdapter(allEntries, getContext());
//        entyViewAdapter.setHasStableIds(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        entryView.setLayoutManager(layoutManager);
        entryView.setAdapter(entyViewAdapter);


    }

    private class GetAllEntriesAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            AppDataBase db = Room.databaseBuilder(getContext(), AppDataBase.class, "CalendarEntry").build();

            allEntries = db.calendarEntryDao().getAllEntriesOrderedByDate();
            return null;

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            setEntriesView();
        }
    }


}