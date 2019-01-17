package ml.ajwad.thisable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class StopListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Stop> stopList = null;
    private ArrayList<Stop> arraylist;

    public StopListViewAdapter(Context context, List<Stop> stopList) {
        mContext = context;
        this.stopList = stopList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Stop>();
        this.arraylist.addAll(stopList);
    }

    public class ViewHolder {
        TextView stopName;
        TextView latitude;
        TextView longitude;
    }

    @Override
    public int getCount() {
        return stopList.size();
    }

    @Override
    public Stop getItem(int position) {
        return stopList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_sel_stop, null);
            // Locate the TextViews in listview_item.xml
            holder.stopName = view.findViewById(R.id.stopName);
            holder.latitude = view.findViewById(R.id.latLabel);
            holder.longitude= view.findViewById(R.id.longLabel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.stopName.setText(stopList.get(position).getName());
        holder.latitude.setText(stopList.get(position).getLatitude().toString());
        holder.longitude.setText(stopList.get(position).getLongitude().toString());

        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SelectStop.class);
                // Pass all data rank
                intent.putExtra("stopName", (stopList.get(position).getName()));
                // Pass all data country
                intent.putExtra("latitude", (stopList.get(position).getLatitude()));
                // Pass all data population
                intent.putExtra("longitude", (stopList.get(position).getLongitude()));
                // Pass all data flag
                // Start SingleItemView Class
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        stopList.clear();
        if (charText.length() == 0) {
            stopList.addAll(arraylist);
        } else {
            for (Stop stop : arraylist) {
                if (stop.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    stopList.add(stop);
                }
            }
        }
        notifyDataSetChanged();
    }
}

