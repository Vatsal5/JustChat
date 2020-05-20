package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<UserDetailWithStatus> implements Filterable {

        private final Context context;
        CircleImageView iv;
        ImageView ivSelected;
        FirebaseDatabase database;
        DatabaseReference reference;
        private ArrayList<UserDetailWithStatus> Filteredlist;
        ArrayList<UserDetailWithStatus> list;

        public interface itemSelected
        {
            public void onItemSelected(String key);
        }

        itemSelected Activity;

        public FriendsAdapter(@NonNull Context context, ArrayList<UserDetailWithStatus>list) {
            super(context, R.layout.friends_list_layout,list);

            this.context = context;
            this.Filteredlist = list;
            this.list = list;
            Activity = (itemSelected) context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v= inflater.inflate(R.layout.friends_list_layout,parent,false);
            iv=v.findViewById(R.id.imageView);
            database=FirebaseDatabase.getInstance();
            reference=database.getReference();
            TextView tvStatus= v.findViewById(R.id.tvstatus);
            ivSelected=v.findViewById(R.id.ivSelected);

           // Log.d("tag",FilteredList.get(position).getUrl());


            if(Filteredlist.get(position).getSelected()==1)
            {
                ivSelected.setVisibility(View.VISIBLE);
            }
            if(Filteredlist.get(position).getUrl().equals("null"))
            {
                iv.setImageResource(R.drawable.person);
//
            }
            else
            {
                Glide.with(context).load(Filteredlist.get(position).getUrl()).into(iv);}



            TextView tvUserName= v.findViewById(R.id.tv_username);


            if((Filteredlist.get(position).getuID().equals("")))
            tvUserName.setText(Filteredlist.get(position).getPh_number());
            else
                tvUserName.setText(Filteredlist.get(position).getuID());
            if(Filteredlist.get(position).getStatus().equals(""))
            {
                tvStatus.setVisibility(View.GONE);
            }
            else
            tvStatus.setText(Filteredlist.get(position).getStatus());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity.onItemSelected(Filteredlist.get(position).getPh_number());

                }
            });

            return v;
        }

    @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String query = charSequence.toString();

                    ArrayList<UserDetailWithStatus> filtered = new ArrayList<>();

                    if (query.isEmpty()) {
                        filtered = list;
                    } else {
                        for (UserDetailWithStatus user : list) {
                            if (user.getuID().toLowerCase().contains(query.toLowerCase()) || user.getPh_number().contains(query)) {
                                filtered.add(user);
                            }
                        }
                    }

                    FilterResults results = new FilterResults();
                    results.count = filtered.size();
                    results.values = filtered;

                    for(int i=0;i<filtered.size();i++)
                        Log.d("hhjj",filtered.get(i).getuID());

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults results) {
                    Filteredlist.clear();
                    Filteredlist.addAll((ArrayList<UserDetailWithStatus>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }

