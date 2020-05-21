package com.androidstudio.chattingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.viewholder>  implements Filterable {

    private final Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    private ArrayList<UserDetailWithStatus> Filteredlist;
    ArrayList<UserDetailWithStatus> Originallist;

    FriendsAdapter.ValueFilter valueFilter;

    public interface itemSelected
    {
        public void onItemSelected(String key);
    }

    FriendsAdapter.itemSelected Activity;

    public FriendsAdapter(@NonNull Context context, ArrayList<UserDetailWithStatus>list) {

        this.context = context;
        this.Filteredlist = list;
        this.Originallist = list;
        Activity = (FriendsAdapter.itemSelected) context;
    }

    public  class  viewholder extends RecyclerView.ViewHolder
    {

        CircleImageView iv;
        ImageView ivSelected;
        TextView tvStatus;
        LinearLayout llfl;
        TextView tvUserName;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.imageView);
            database=FirebaseDatabase.getInstance();
            reference=database.getReference();
            llfl=itemView.findViewById(R.id.llfl);
             tvStatus= itemView.findViewById(R.id.tvstatus);
            ivSelected=itemView.findViewById(R.id.ivSelected);
             tvUserName = itemView.findViewById(R.id.tv_username);}
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v= inflater.inflate(R.layout.friends_list_layout,parent,false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {


            if (Filteredlist.get(holder.getAdapterPosition()).getSelected() == 1) {
                holder.ivSelected.setVisibility(View.VISIBLE);
            }
            if (Filteredlist.get(holder.getAdapterPosition()).getUrl().equals("null")) {
                holder.iv.setImageResource(R.drawable.person);
//
            } else {
                Glide.with(context).load(Filteredlist.get(position).getUrl()).into(holder.iv);
            }





            if ((Filteredlist.get(position).getuID().equals("")))
                holder.tvUserName.setText(Filteredlist.get(position).getPh_number());
            else
                holder.tvUserName.setText(Filteredlist.get(position).getuID());
//            if (Filteredlist.get(position).getStatus().equals("")) {
//                holder.tvStatus.setVisibility(View.GONE);
//            } else
                holder.tvStatus.setText(Filteredlist.get(position).getStatus());

            holder.llfl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Filteredlist.get(holder.getAdapterPosition()).getKey()==null)
                        Activity.onItemSelected(Filteredlist.get(holder.getAdapterPosition()).getPh_number());
                    else
                        Activity.onItemSelected(Filteredlist.get(holder.getAdapterPosition()).getKey());
                }
            });
        }



    @Override
    public int getItemCount() {
        return Filteredlist.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new FriendsAdapter.ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<UserDetailWithStatus> filterList = new ArrayList<>();
                for (int i = 0; i < Originallist.size(); i++) {
                    if ((Originallist.get(i).getuID().toUpperCase()).contains(constraint.toString().toUpperCase()) || (Originallist.get(i).getKey() ==null &&Originallist.get(i).getPh_number().contains(constraint))) {
                        filterList.add(Originallist.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = Originallist.size();
                results.values = Originallist;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            Filteredlist = (ArrayList<UserDetailWithStatus>) results.values;
            notifyDataSetChanged();
        }
    }
}
