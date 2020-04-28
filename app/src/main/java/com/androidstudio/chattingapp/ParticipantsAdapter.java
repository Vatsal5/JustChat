package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ParticipantsAdapter  extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    Context context;
    ArrayList<UserDetailWithStatus> users;
    SharedPreferences pref;
    ParticipantsAdapter.itemSelected Activity;

    ParticipantsAdapter(Context context, ArrayList<UserDetailWithStatus> users) {
        this.context = context;
        this.users = users;
        pref = context.getSharedPreferences("Names", 0);
        Activity = (ParticipantsAdapter.itemSelected) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvGroupAdmin;
        ImageView ivProfile;
        LinearLayout llParticipants;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvGroupAdmin = itemView.findViewById(R.id.tvGroupAdmin);
            llParticipants = itemView.findViewById(R.id.llParticipants);
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
    public void onBindViewHolder(@NonNull ParticipantsAdapter.ViewHolder holder,final int position) {

        if(!users.get(position).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
//            if (pref.getString(users.get(position).getPh_number(), "null").equals("null"))
//                holder.tvName.setText(users.get(position).getPh_number());
//            else
//                holder.tvName.setText(pref.getString(users.get(position).getPh_number(), "null"));
            holder.tvName.setText(pref.getString(users.get(position).getPh_number(),users.get(position).getPh_number()));
        }else
            holder.tvName.setText("You");


        if(!(users.get(position).getUrl()==null))
            Glide.with(context).load(users.get(position).getUrl()).into(holder.ivProfile);
        else
            Glide.with(context).load(R.drawable.person).into(holder.ivProfile);

        if(users.get(position).getStatus()!=null)
            holder.tvGroupAdmin.setVisibility(View.VISIBLE);
        else
            holder.tvGroupAdmin.setVisibility(View.GONE);

        holder.llParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity.onItemSelected(position);
            }
        });

    }

    public interface itemSelected
    {
        public void onItemSelected(int index);

    }
    @Override
    public int getItemCount() {
        return users.size();
    }
}
