package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

public class Profile extends AppCompatActivity implements profile_listitem_adapter.itemSelected {

    ImageView ivProfile,ivClick;
    ListView list;
    profile_listitem_adapter adapter;
    ArrayList<String> data;

    LayoutInflater inflater;
    LinearLayout llProfile;

    TextView tvHeading,tvSave,tvCancel;
    EditText etData;
    PopupWindow window;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        data = new ArrayList<>();

        data.add("Vatsal");
        data.add("Work hard in Silence and let success make noise");
        data.add(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        ivProfile = findViewById(R.id.ivProfile);
        ivClick = findViewById(R.id.ivClick);

        llProfile = findViewById(R.id.llProfile);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_layout,null,false);

        tvHeading = view.findViewById(R.id.tvHeading);
        tvSave = view.findViewById(R.id.tvSave);
        tvCancel = view.findViewById(R.id.tvCancel);
        etData = view.findViewById(R.id.etData);


        list = findViewById(R.id.list);
        adapter = new profile_listitem_adapter(Profile.this,data);
        list.setAdapter(adapter);

        window = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
    }

    @Override
    public void onItemSelected(final int index) {
        switch (index)
        {
            case 0:
                tvHeading.setText("Enter Your Name");
                window.showAtLocation(llProfile,Gravity.BOTTOM,0,0);
                break;
            case 1:
                tvHeading.setText("Enter your status");
                window.showAtLocation(llProfile,Gravity.BOTTOM,0,0);
                break;
        }
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(index);
                data.add(index,etData.getText().toString().trim());
                adapter.notifyDataSetChanged();
                window.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
            }
        });

        etData.setText(data.get(index));
        etData.setSelection(data.get(index).length());
    }
}
