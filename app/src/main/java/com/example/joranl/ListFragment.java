package com.example.joranl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    RecyclerView entryView;

    private FloatingActionButton addEntryFAB;
    List<EntryWithBItMap> entryWithBItMaps;
    List<EntryWithBItMap> filteredEntries;
    EntyViewAdapter entyViewAdapter;

    public ListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        addEntryFAB = view.findViewById(R.id.addEntryFAB);
        entryView = view.findViewById(R.id.entryView);

        getEntries();

        addEntryFAB.setOnClickListener(v -> startActivity(new Intent(getActivity(), EntryActivity.class)));
        CardView cardView = view.findViewById(R.id.cardView);

        // Set an OnClickListener on the CardView
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When CardView is clicked, open the SearchView and show the keyboard
                SearchView searchView = view.findViewById(R.id.search_bar);
                searchView.setIconified(false);
            }
        });

        SearchView searchView = view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filteredEntries = new ArrayList<>();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredEntries = new ArrayList<>();
                filterEntries(newText);
                return false;
            }
        });

        return view;
    }
    private void filterEntries(String query) {
        filteredEntries.clear();

        // If the query is empty, show all entries
        if (query.isEmpty()) {
            filteredEntries.addAll(entryWithBItMaps);
        } else {
            // Otherwise, filter entries based on the query
            for (EntryWithBItMap entry : entryWithBItMaps) {
                if (entry.getEntry().getEntryDate().toLowerCase().contains(query.toLowerCase())) {
                    filteredEntries.add(entry);
                }
            }
        }

        // Update the adapter with the filtered entries
        entyViewAdapter.filterList(filteredEntries);
    }


    @Override
    public void onResume() {
        super.onResume();
        getEntries();
    }

    private void getEntries() {
        new GetAllEntriesAsyncTask().execute();
    }

    public void setEntriesView() {

        entyViewAdapter = new EntyViewAdapter(entryWithBItMaps, getContext());
//        entyViewAdapter.setHasStableIds(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        entryView.setHasFixedSize(true);
        entryView.setLayoutManager(layoutManager);
        entryView.setAdapter(entyViewAdapter);


    }

    private byte[] readBytesFromFile(String imgUri) throws IOException {
        FileInputStream fileInputStream = getContext().openFileInput(imgUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private class GetAllEntriesAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            AppDataBase db = Room.databaseBuilder(getContext(), AppDataBase.class, "CalendarEntry").build();

            entryWithBItMaps = new ArrayList<>();

            for (CalendarEntry entry : db.calendarEntryDao().getAllEntriesOrderedByDate()) {
                if (entry.hasImage()) {
                    byte[] bytes;
                    try {
                        bytes = readBytesFromFile(entry.getImgUri());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                    int desiredWidth = 100; // Set your desired width here
                    int desiredHeight = 100;

                    options.inSampleSize = calculateInSampleSize(options, desiredWidth, desiredHeight);
                    options.inJustDecodeBounds = false;

                    Bitmap resizedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                    entryWithBItMaps.add(new EntryWithBItMap(entry, resizedBitmap));
                } else {
                    entryWithBItMaps.add(new EntryWithBItMap(entry, null));
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            setEntriesView();
        }
    }


}