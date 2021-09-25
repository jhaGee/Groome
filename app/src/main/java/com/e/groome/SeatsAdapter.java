package com.e.groome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SeatsAdapter extends RecyclerView.Adapter<SeatsAdapter.MyViewHolder> {
    Context context;

    private List<Seats> seatsList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView seats, status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            seats = itemView.findViewById(R.id.list_seats);
            status = itemView.findViewById(R.id.list_status);
        }
    }

    public SeatsAdapter(List<Seats> seatsList){
        this.seatsList = seatsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seats_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Seats seats = seatsList.get(position);

        holder.seats.setText(seats.getSeat());

        if (seats.isFree()){
            holder.status.setText("Free");
            holder.status.setBackgroundResource(R.drawable.free_bg);
        }
        if (seats.isNotFree()){
            holder.status.setText("Not Free");
            holder.status.setBackgroundResource(R.drawable.not_free_bg);
        }
    }

    @Override
    public int getItemCount() {
        return seatsList.size();
    }

}
