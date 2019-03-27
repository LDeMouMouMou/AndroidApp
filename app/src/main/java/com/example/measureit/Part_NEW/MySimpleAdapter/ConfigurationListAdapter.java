package com.example.measureit.Part_NEW.MySimpleAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.measureit.R;

import java.util.HashMap;
import java.util.List;

public class ConfigurationListAdapter extends SimpleAdapter {
    private Context context;
    private List<HashMap<String, Object>> listItem;
    private LayoutInflater layoutInflater;

    class ListItemView{
        public TextView name;
        public TextView time;
        public ImageView chosen;
    }

    public ConfigurationListAdapter(Context context,
                                List<HashMap<String, Object>> data,
                                int resource,
                                String[] from, int[] to)
    {
        super(context, data, resource, from, to);
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        listItem = data;
    }

    public int getCount(){
        return listItem.size();
    }

    public Object getItem(int position){
        return listItem.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ConfigurationListAdapter.ListItemView listItemView;
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.configurationlist_item, null);
            listItemView = new ListItemView();
            listItemView.name = convertView.findViewById(R.id.configurationname);
            listItemView.time = convertView.findViewById(R.id.configurationtime);
            listItemView.chosen = convertView.findViewById(R.id.configurationChosen);
            convertView.setTag(listItemView);
        }
        else {
            listItemView = (ConfigurationListAdapter.ListItemView) convertView.getTag();
        }
        //
        listItemView.name.setText(String.valueOf(listItem.get(position).get("name")));
        listItemView.time.setText(String.valueOf(listItem.get(position).get("time")));
        if (position == 0){
            listItemView.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            listItemView.name.setGravity(Gravity.CENTER);
            listItemView.time.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            listItemView.time.setGravity(Gravity.CENTER);
            listItemView.chosen.setImageResource(R.drawable.connected);
        }
        return convertView;
    }

}
