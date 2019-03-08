package ml.ajwad.thisable;

import android.app.Activity;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

public class SearchBus extends Activity {

    ArrayList<String> busStop = new ArrayList<>();
    Button mButton;
    ListView listView;
    BusListViewAdapter busListViewAdapter;
    ArrayList<Route> routeList = new ArrayList<>();
    AutoCompleteTextView source, destination;
    DatabaseHelper myDBHelper;
    SQLiteDatabase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus);
        mButton = findViewById(R.id.searchButton);
        myDBHelper = new DatabaseHelper(this);
        try {
            myDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDBHelper.openDataBase();
        }catch(SQLException sqle) {
            throw sqle;
        }
        myDB = myDBHelper.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT NAME FROM BUS_STOPS", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                busStop.add(name);
                cursor.moveToNext();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, busStop);
        source = findViewById(R.id.sourceBox);
        source.setThreshold(1);//will start working from first character
        source.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        destination = findViewById(R.id.destBox);
        destination.setThreshold(1);//will start working from first character
        destination.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        listView = findViewById(R.id.routeSearchList);
        busListViewAdapter= new BusListViewAdapter(this, routeList);
        listView.setAdapter(busListViewAdapter);
        mButton.setOnClickListener(searchButtonListener);
    }

    private View.OnClickListener searchButtonListener = v -> {
        routeList.clear();
        Log.d("DEBUG","Button Pressed");
        String qSource = source.getText().toString();
        String qDest = destination.getText().toString();
        Cursor cursor = myDB.rawQuery("SELECT ROUTE_ID FROM 'ROUTE_INFO' WHERE ROUTE_ID IN (SELECT ALL ROUTE_ID FROM 'ROUTE_INFO' WHERE STOP == ?) AND STOP == ? ", new String[]{qDest, qSource});
        Log.d("DEBUG", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Integer routeID = cursor.getInt(cursor.getColumnIndex("ROUTE_ID"));
                Log.d("CURSOR",routeID.toString());
                Route rt = new Route(routeID, qSource, qDest);
                routeList.add(rt);
                cursor.moveToNext();
            }
        }
        cursor.close();
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {

        }
    };
}
