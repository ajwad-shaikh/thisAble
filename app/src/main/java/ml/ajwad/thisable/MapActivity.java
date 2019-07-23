package ml.ajwad.thisable;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String MARKERS = "markers";
    private static final String POLY = "poly_lines";

    DatabaseHelper myDBHelper;

    private static final String TAG = MapActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private HashMap<String, Polyline> mPoly = new HashMap<>();
    private HashMap<String, Marker> mStops = new HashMap<>();
    private GoogleMap mMap;
    private Bitmap smallMarker;
    private Bitmap stopMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
            mMarkers = (HashMap<String, Marker>)savedInstanceState.getSerializable(MARKERS);
            mPoly = (HashMap<String, Polyline>)savedInstanceState.getSerializable(POLY);
        }
        setContentView(R.layout.activity_map);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(MARKERS, mMarkers);
        outState.putSerializable(POLY, mPoly);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.invert_eye);
        Bitmap b=bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
        bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.stop_icon);
        b = bitmapdraw.getBitmap();
        stopMarker = Bitmap.createScaledBitmap(b, 75, 75, false);
        getDataBaseData();
        loginToFirebase();
        simulateRoute();
    }

    private  void getDataBaseData() {
        myDBHelper = new DatabaseHelper(this);
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
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                Double latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                Double longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                LatLng location = new LatLng(latitude, longitude);
                mStops.put(name, mMap.addMarker(new MarkerOptions()
                        .title(name)
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(stopMarker))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        mMap.setOnMapLoadedCallback(() -> setBounds(mStops));
    }

    private void setBounds(HashMap<String, Marker> markerHashMap) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerHashMap.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void simulateRoute() {
        LatLng locationCenter = new LatLng(22.522379, 88.401295);
        Marker routeOne = mMap.addMarker(new MarkerOptions()
                .title("Route 1")
                .position(locationCenter)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        Polyline routeOneTrack = mMap.addPolyline(new PolylineOptions()
                .add(locationCenter)
                .width(5)
                .color(Color.BLUE));
        List<LatLng> pointsOne = new ArrayList<>();
        pointsOne.add(locationCenter);
        routeOneTrack.setPoints(pointsOne);
        final Handler handler = new Handler();
        Runnable task = new Runnable() {
                @Override
                public void run() {
                    LatLng currLocation = routeOne.getPosition();
                    Double latitude = currLocation.latitude + (1 * (Math.random() * Math.random()) % 0.0005);
                    Double longitude = currLocation.longitude + (-1  * (Math.random() * Math.random()) % 0.0005);
                    LatLng nextLocation = new LatLng(latitude, longitude);
                    routeOne.setPosition(nextLocation);
                    pointsOne.add(nextLocation);
                    routeOneTrack.setPoints(pointsOne);
                    handler.postDelayed(this, 1500);
                }
        };
        handler.post(task);
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions()
                    .title(key)
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))));
            mPoly.put(key, mMap.addPolyline(new PolylineOptions()
                    .add(location)
                    .width(5)
                    .color(Color.BLUE)));

        } else {
            mMarkers.get(key).setPosition(location);
            List<LatLng> points = mPoly.get(key).getPoints();
            points.add(location);
            mPoly.get(key).setPoints(points);
        }
    }

    private Double plusMinus() {
        Double random = Math.random();
        random -= 0.5;
        return Math.signum(random);
    };
}

