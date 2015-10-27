package com.example.mayank.smarthome.adapter;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayank.smarthome.R;
import com.example.mayank.smarthome.model.Recommendation;

import java.util.List;


public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {

        CardView cv2;
        TextView RecommendationRoom;
        TextView RecommendationText;
        ImageView RecommendationRoomPhoto;

        RecommendationViewHolder(View itemView) {

            super(itemView);
            cv2 = (CardView) itemView.findViewById(R.id.cv2);
            RecommendationRoom = (TextView) itemView.findViewById(R.id.recommendation_room_name);
            RecommendationText = (TextView) itemView.findViewById(R.id.recommendation_action);
            RecommendationRoomPhoto = (ImageView) itemView.findViewById(R.id.recommendation_photo);

        }
    }

    List<Recommendation> recommendations;

    public RecommendationAdapter(List<Recommendation> recommendations) {
        this.recommendations = recommendations;

    }

    @Override
    public RecommendationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendations_card_view_layout, parent, false);
        RecommendationViewHolder h = new RecommendationViewHolder(v);

        return h;
    }

    @Override
    public void onBindViewHolder(RecommendationViewHolder holder, int position) {

        //  holder.roomName.setText(rooms.get(position).name);
        // holder.roomTime.setText(rooms.get(position).time);
        // holder.roomPhoto.setImageResource(rooms.get(position).photoId);

        holder.RecommendationRoom.setText(recommendations.get(position).recommendationRoomName);
        holder.RecommendationText.setText(recommendations.get(position).recommendationText);
        holder.RecommendationRoomPhoto.setImageResource(recommendations.get(position).recommendationRoomPhotoId);
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
