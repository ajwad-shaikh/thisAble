package ml.ajwad.thisable;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SelectStop extends Activity {

    ListView listView;
    StopListViewAdapter stopListViewAdapter;
    EditText editsearch;
    ArrayList<Stop> arraylist = new ArrayList<Stop>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stop);
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
        Cursor cursor = myDB.rawQuery("SELECT NAME, LATITUDE, LONGITUDE FROM BUS_STOPS", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String stopName = cursor.getString(cursor.getColumnIndex("NAME"));
                Double latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                Double longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                Stop sp = new Stop(stopName, latitude, longitude);
                arraylist.add(sp);
                cursor.moveToNext();
            }
        }
        cursor.close();
        listView = findViewById(R.id.stopList);
        stopListViewAdapter = new StopListViewAdapter(this, arraylist);
        listView.setAdapter(stopListViewAdapter);
        editsearch = findViewById(R.id.stopBox);
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
                stopListViewAdapter.filter(text);
            }
        });
    }
}
