package com.androidstudio.chattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ParticipantsAdapter  extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    Context context;
    ArrayList<UserDetailWithStatus> users;

    ParticipantsAdapter(Context context, ArrayList<UserDetailWithStatus> users) {
        this.context = context;
        this.users = users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvStatus;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivProfile = itemView.findViewById(R.id.ivProfile);

        }
    }

    @NonNull
    @Override
    public ParticipantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.participants_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantsAdapter.ViewHolder holder, int position) {

        holder.tvName.setText(users.get(position).getPh_number());
        holder.tvStatus.setText(users.get(position).getStatus());

        if(!(users.get(position).getUrl()==null))
            Glide.with(context).load(users.get(position).getUrl()).into(holder.ivProfile);
        else
            Glide.with(context).load(R.drawable.person).into(holder.ivProfile);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
