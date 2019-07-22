package ml.ajwad.thisable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    GridLayout mainGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainGrid = findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);

        CardView assist = findViewById(R.id.assistCard);
        assist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pass Intent to get Assistance
                Toast.makeText(MainActivity.this, "Getting Assistance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSingleEvent(GridLayout mainGrid){
        for(int i = 0; i < mainGrid.getChildCount(); i++){
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int finali = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finali==0){
                        Intent intent = new Intent(MainActivity.this, SearchBus.class);
                        startActivity(intent);
                    }
                    else if(finali==1){
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                    }
                    else if(finali==2){
                        Intent intent = new Intent(MainActivity.this, SelectRoute.class);
                        startActivity(intent);
                    }
                    else if(finali==3){
                        Intent intent = new Intent(MainActivity.this, SelectStop.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

}
