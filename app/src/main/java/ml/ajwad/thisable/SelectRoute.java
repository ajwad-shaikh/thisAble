package ml.ajwad.thisable;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SelectRoute extends Activity {

    ListView listView;
    RouteListViewAdapter routeListViewAdapter;
    EditText editsearch;
    ArrayList<Route> arraylist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        DatabaseHelper myDBHelper = new DatabaseHelper(this);
        try {
            myDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDBHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        SQLiteDatabase myDB = myDBHelper.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("SELECT ROUTE_ID, SOURCE, DESTINATION FROM BUS_ROUTES", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Integer routeID = cursor.getInt(cursor.getColumnIndex("ROUTE_ID"));
                String source = cursor.getString(cursor.getColumnIndex("SOURCE"));
                String destination = cursor.getString(cursor.getColumnIndex("DESTINATION"));
                Route rt = new Route(routeID, source, destination);
                arraylist.add(rt);
                cursor.moveToNext();
            }
        }
        cursor.close();
        listView = findViewById(R.id.routeList);
        routeListViewAdapter = new RouteListViewAdapter(this, arraylist);
        listView.setAdapter(routeListViewAdapter);
        editsearch = findViewById(R.id.routeBox);
        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                routeListViewAdapter.filter(text);
            }
        });
    }
}
