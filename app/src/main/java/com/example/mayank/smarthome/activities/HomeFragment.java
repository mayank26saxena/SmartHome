package com.example.mayank.smarthome.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayank.smarthome.R;
import com.example.mayank.smarthome.adapter.RoomsRVAdapter;
import com.example.mayank.smarthome.model.MyDBHandler;
import com.example.mayank.smarthome.model.Room;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Room> rooms;
    private RecyclerView rv;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void initializeData() {

        MyDBHandler dbHandler;
        dbHandler = new MyDBHandler(this.getContext());
        rooms = new ArrayList<>();

        int rooms_count = dbHandler.getProfilesCount();

        if (rooms_count == 0)
            return;
        else {

            Cursor c = dbHandler.getAllRooms();
            if (c.moveToFirst()) {

                while (c.isAfterLast() == false) {
                    String name = c.getString(0);
                    String time = c.getString(1);

                    rooms.add(new Room(name, "Last Connected at : " + time, R.raw.house));
                    c.moveToNext();
                }
            }

        }

        // rooms.add(new Room("Drawing Room", "Last Connected : 1 AM", R.raw.house));
        // rooms.add(new Room("Bedroom", "Last connected : 9 AM", R.raw.house));
        // rooms.add(new Room("Hall", "Last connected : 11 AM", R.raw.house));
    }

    private void initializeAdapter() {
        RoomsRVAdapter adapter = new RoomsRVAdapter(rooms);
        rv.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}