package ml.ajwad.thisable;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

public class SearchBus extends Activity {

    ArrayList<String> busStop = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus);
        DatabaseHelper myDBHelper = new DatabaseHelper(this);
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
        SQLiteDatabase myDB = myDBHelper.getReadableDatabase();
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
        AutoCompleteTextView source = findViewById(R.id.sourceBox);
        source.setThreshold(1);//will start working from first character
        source.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        AutoCompleteTextView destination = findViewById(R.id.destBox);
        destination.setThreshold(1);//will start working from first character
        destination.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }
}
