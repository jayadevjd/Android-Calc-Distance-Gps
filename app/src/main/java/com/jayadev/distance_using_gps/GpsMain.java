package com.jayadev.distance_using_gps;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jayadev on 20/11/15.
 */
public class GpsMain extends Activity {
    private Intent intent;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                intent = new Intent(GpsMain.this, GpsService.class);
                startService(intent);

            }
        });

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String distance = intent.getStringExtra("distance");
            textView.setText("Distance is " + distance+" M" );
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GpsService.BROADCAST_ACTION));
    }
}
