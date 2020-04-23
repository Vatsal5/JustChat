package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
        SharedPreferences preftheme;

        ivImage = v.findViewById(R.id.ivImage);
        tvData = v.findViewById(R.id.tvData);
        tvHeading =v.findViewById(R.id.tvHeading);
        preftheme=context.getSharedPreferences("theme",0);
        String theme=preftheme.getString("theme","red");




        switch (theme) {
            case "orange":

                ivImage.setColorFilter(context.getResources().getColor(R.color.Orange));
                tvData.setTextColor(context.getResources().getColor(R.color.Orange));
                break;

            case "blue":

                ivImage.setColorFilter(context.getResources().getColor(R.color.blue));
                tvData.setTextColor(context.getResources().getColor(R.color.blue));

                break;


            case "bluish":
                ivImage.setColorFilter(context.getResources().getColor(R.color.bluish));
                tvData.setTextColor(context.getResources().getColor(R.color.bluish));
                break;


            case "deepred":
                ivImage.setColorFilter(context.getResources().getColor(R.color.deepred));
                tvData.setTextColor(context.getResources().getColor(R.color.deepred));
                break;

            case "faintpink":
                ivImage.setColorFilter(context.getResources().getColor(R.color.faintpink));
                tvData.setTextColor(context.getResources().getColor(R.color.faintpink));

                break;

            case "darkblue":
                ivImage.setColorFilter(context.getResources().getColor(R.color.darkblue));
                tvData.setTextColor(context.getResources().getColor(R.color.darkblue));
                break;


            case "green":
                ivImage.setColorFilter(context.getResources().getColor(R.color.green));
                tvData.setTextColor(context.getResources().getColor(R.color.green));
                break;

            case "lightorange":
                ivImage.setColorFilter(context.getResources().getColor(R.color.lightorange));
                tvData.setTextColor(context.getResources().getColor(R.color.lightorange));

                break;

            case "lightred":
                ivImage.setColorFilter(context.getResources().getColor(R.color.lightred));
                tvData.setTextColor(context.getResources().getColor(R.color.lightred));
                break;


            case "mustard":
                ivImage.setColorFilter(context.getResources().getColor(R.color.mustard));
                tvData.setTextColor(context.getResources().getColor(R.color.mustard));
                break;

            case "pink":
                ivImage.setColorFilter(context.getResources().getColor(R.color.pink));
                tvData.setTextColor(context.getResources().getColor(R.color.pink));
                break;

            case "pureorange":
                ivImage.setColorFilter(context.getResources().getColor(R.color.pureorange));
                tvData.setTextColor(context.getResources().getColor(R.color.pureorange));
                break;

            case "purepink":
                ivImage.setColorFilter(context.getResources().getColor(R.color.purepink));
                tvData.setTextColor(context.getResources().getColor(R.color.purepink));
                break;

            case "purple":
                ivImage.setColorFilter(context.getResources().getColor(R.color.purple));
                tvData.setTextColor(context.getResources().getColor(R.color.purple));
                break;

            default:
                ivImage.setColorFilter(context.getResources().getColor(R.color.red));
                tvData.setTextColor(context.getResources().getColor(R.color.red));
        }


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
