package com.example.LTPT.User;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.LTPT.R;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> maintitle;

    public MyListAdapter(Activity context, ArrayList<String> maintitle) {
        super(context, R.layout.mylist, maintitle);
        this.context=context;
        this.maintitle=maintitle;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = rowView.findViewById(R.id.title);
        ImageView imageView = rowView.findViewById(R.id.icon);
        titleText.setText(maintitle.get(position));
        if (position==0){
            imageView.setImageResource(R.drawable.startldpi);}
        else if (position==(maintitle.size()-1)){imageView.setImageResource(R.drawable.endldpi);}
        else {imageView.setImageResource(R.drawable.intermediateldpi);}
        return rowView;
    }

}
