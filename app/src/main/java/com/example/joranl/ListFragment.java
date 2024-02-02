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
import java.util.Objects;


public class ListFragment extends Fragment {

    private RecyclerView entryView;
    private List<EntryWithBItMap> entryWithBItMaps;
    private List<EntryWithBItMap> filteredEntries;
    private EntyViewAdapter entyViewAdapter;

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        FloatingActionButton addEntryFAB = view.findViewById(R.id.addEntryFAB);
        entryView = view.findViewById(R.id.entryView);

        getEntries();

        addEntryFAB.setOnClickListener(v -> startActivity(new Intent(getActivity(), EntryActivity.class)));
        CardView cardView = view.findViewById(R.id.cardView);

        // Set an OnClickListener on the CardView
        cardView.setOnClickListener(v -> {
            // When CardView is clicked, open the SearchView and show the keyboard
            SearchView searchView = view.findViewById(R.id.search_bar);
            searchView.setIconified(false);
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
                if (entyViewAdapter != null) {
                    filterEntries(newText);
                }
                return false;
            }
        });

        return view;
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

    public void setEntriesView() {
        entyViewAdapter = new EntyViewAdapter(entryWithBItMaps, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        entryView.setLayoutManager(layoutManager);
        entryView.setAdapter(entyViewAdapter);
    }

    private void getEntries() {
        new GetAllEntriesAsyncTask().execute();
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

    @Override
    public void onResume() {
        super.onResume();
        getEntries();
    }

    private class GetAllEntriesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AppDataBase db = Room.databaseBuilder(requireContext(), AppDataBase.class, "CalendarEntry").build();

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

                    int width = 100;
                    int height = 100;

                    options.inSampleSize = calculateInSampleSize(options, width, height);
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