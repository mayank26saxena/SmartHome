package com.example.mayank.smarthome.activities;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayank.smarthome.R;
import com.example.mayank.smarthome.adapter.RecommendationAdapter;
import com.example.mayank.smarthome.model.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class RecommendationFragment extends Fragment{

    private RecyclerView rv2;
    private List<Recommendation> recommendations;


    public RecommendationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommendations, container, false);

        rv2 = (RecyclerView) rootView.findViewById(R.id.rv2);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv2.setLayoutManager(llm);

        initializeData();
        intializeAdapter();

        // Inflate the layout for this fragment
        return rootView;
    }

    public void intializeAdapter() {

        RecommendationAdapter adapter = new RecommendationAdapter(recommendations) ;
        rv2.setAdapter(adapter);

    }

    public void initializeData() {

        recommendations = new ArrayList<>();
        recommendations.add(new Recommendation("Dining Room", "Switch on fan", R.raw.house));
        recommendations.add(new Recommendation("Bedroom", "Switch on AC", R.raw.house));
        recommendations.add(new Recommendation("Washroom", "Switch on Geyser", R.raw.house));

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

