package com.example.joranl;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ListFragment extends Fragment {

    private FloatingActionButton addEntryFAB;


    public ListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        addEntryFAB = view.findViewById(R.id.addEntryFAB);

        addEntryFAB.setOnClickListener(v -> startActivity(new Intent(getActivity(), EntryActivity.class)));


        return view;
    }


}