package com.example.measureit.Part_NEW.DataSession;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.measureit.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataConfigAdapter extends ArrayAdapter<DataConfigItem> {

    private int resouceId;
    private Context context;

    public DataConfigAdapter(@NonNull Context context, int resouce, @NonNull List<DataConfigItem> objects) {
        super(context, resouce, objects);
        resouceId = resouce;
        this.context = context;
    }

    @Override
    public boolean isEnabled(int position) {
        DataConfigItem dataConfigItem = getItem(position);
        return !(dataConfigItem.getIsTitle());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DataConfigItem dataConfigItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resouceId, parent, false);
        if (dataConfigItem.getIsTitle()) {
            view.setBackgroundColor(context.getResources().getColor(R.color.colorTransparent));
            ImageView imageView = view.findViewById(R.id.rightArrowImage);
            imageView.setVisibility(View.INVISIBLE);
            TextView titleText = view.findViewById(R.id.menuItemName);
            titleText.setText(dataConfigItem.getItemName());
            titleText.setTextSize(14);
            titleText.setTypeface(Typeface.DEFAULT_BOLD);
            return view;
        }
        TextView textView = view.findViewById(R.id.menuItemName);
        textView.setText(dataConfigItem.getItemName());
        return view;
    }

}
