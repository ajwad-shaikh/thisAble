package ml.ajwad.thisable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusListViewAdapter extends BaseAdapter{
    // Declare Variables
    private Context mContext;
    private LayoutInflater inflater;
    private List<Route> routeList = null;
    private ArrayList<Route> arraylist;

    public BusListViewAdapter(Context context, List<Route> routeList) {
        mContext = context;
        this.routeList = routeList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Route>();
        this.arraylist.addAll(routeList);
    }

    public class ViewHolder {
        TextView routeID;
        TextView source;
        TextView destination;
    }

    @Override
    public int getCount() {
        return routeList.size();
    }

    @Override
    public Route getItem(int position) {
        return routeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_sel_route, null);
            // Locate the TextViews in listview_item.xml
            holder.routeID = view.findViewById(R.id.routeID);
            holder.source = view.findViewById(R.id.sourceLabel);
            holder.destination = view.findViewById(R.id.destLabel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.routeID.setText(routeList.get(position).getRouteID().toString());
        holder.source.setText(routeList.get(position).getSource());
        holder.destination.setText(routeList.get(position).getDestination());

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SelectRoute.class);
                // Pass all data rank
                intent.putExtra("routeID", (routeList.get(position).getRouteID()));
                // Pass all data country
                intent.putExtra("source", (routeList.get(position).getSource()));
                // Pass all data population
                intent.putExtra("destination", (routeList.get(position).getDestination()));
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
        routeList.clear();
        if (charText.length() == 0) {
            routeList.addAll(arraylist);
        } else {
            for (Route route : arraylist) {
                if (route.getRouteID().toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    routeList.add(route);
                }
            }
        }
        notifyDataSetChanged();
    }
}
