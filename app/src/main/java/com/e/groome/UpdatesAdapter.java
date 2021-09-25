package com.e.groome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.MyViewHolder> {

    private List<Updates> updatesList;
    private int freeCount = 0, notFreeCount = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView seats;
        public CheckBox free, notFree;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            seats = itemView.findViewById(R.id.update_list_seats);
            free = itemView.findViewById(R.id.update_list_free);
            notFree = itemView.findViewById(R.id.update_list_notfree);
        }
    }

    public UpdatesAdapter(List<Updates> updatesList){
        this.updatesList = updatesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.update_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdatesAdapter.MyViewHolder holder, final int position) {
        final Updates updates = updatesList.get(position);

        holder.seats.setText(updates.getSeat());
        if (updates.isFree()){
            holder.free.setChecked(true);
            holder.notFree.setChecked(false);
        }
        if (updates.isNotFree()){
            holder.free.setChecked(false);
            holder.notFree.setChecked(true);
        }

        holder.free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    holder.notFree.setChecked(false);
                    Updates newUpdates = new Updates(updates.getSeat(), updates.getKey(), true, false);
                    updatesList.set(holder.getPosition(), newUpdates);
                }else {
                    holder.notFree.setChecked(true);
                }
            }
        });

        holder.notFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    holder.free.setChecked(false);
                    Updates newUpdates = new Updates(updates.getSeat(), updates.getKey(), false, true);
                    updatesList.set(holder.getPosition(), newUpdates);
                }else {
                    holder.free.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return updatesList.size();
    }
}
