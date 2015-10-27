package com.example.mayank.smarthome.adapter;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayank.smarthome.R;
import com.example.mayank.smarthome.model.Room;

import java.util.List;

public class RoomsRVAdapter extends RecyclerView.Adapter<RoomsRVAdapter.RoomViewHolder>{

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView roomName;
        TextView roomTime;
        ImageView roomPhoto;
//        ImageButton editButton ;

        RoomViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            roomName = (TextView)itemView.findViewById(R.id.room_name);
            roomTime = (TextView)itemView.findViewById(R.id.room_time);
            roomPhoto = (ImageView)itemView.findViewById(R.id.room_photo);
           // editButton = (ImageButton) itemView.findViewById(R.id.editbutton) ;
        }

    }

    List<Room> rooms;

    public RoomsRVAdapter(List<Room> rooms){
        this.rooms = rooms;

    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_card_view_layout, parent, false);
        RoomViewHolder rvh = new RoomViewHolder(v);
        return rvh;    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {

        //personViewHolder.personName.setText(persons.get(i).name);
        //personViewHolder.personAge.setText(persons.get(i).age);
        //personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);

        holder.roomName.setText(rooms.get(position).name);
        holder.roomTime.setText(rooms.get(position).time);
        holder.roomPhoto.setImageResource(rooms.get(position).photoId);
//        holder.editButton.setImageResource(rooms.get(position).editButtonPhotoId);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
