package com.androidstudio.chattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class profile_listitem_adapter extends ArrayAdapter
{
    Context context;
    ArrayList<String> data;

    public interface itemSelected
    {
        public void onItemSelected(int index);
    }

    itemSelected Activity;

    public profile_listitem_adapter(@NonNull Context context,ArrayList<String>list) {
        super(context,R.layout.profile_listitem_layout,list);

        this.context = context;
        this.data = list;
        Activity = (itemSelected) context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v= inflater.inflate(R.layout.profile_listitem_layout,parent,false);

        ImageView ivImage;
        TextView tvData,tvHeading;

        ivImage = v.findViewById(R.id.ivImage);
        tvData = v.findViewById(R.id.tvData);
        tvHeading =v.findViewById(R.id.tvHeading);

        switch (position)
        {
            case 0:
                ivImage.setImageResource(R.drawable.person1);
                tvHeading.setText("Name");
                tvData.setText(data.get(position));
                break;
            case 1:
                ivImage.setImageResource(R.drawable.about);
                tvHeading.setText("About");
                tvData.setText(data.get(position));
                break;
            case 2:
                ivImage.setImageResource(R.drawable.phone);
                tvHeading.setText("Phone Number");
                tvData.setText(data.get(position));
                break;
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity.onItemSelected(position);
            }
        });

        return v;
    }
}
