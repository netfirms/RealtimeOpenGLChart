package com.taweechai.realtimeopenglchart;

import java.util.Timer;

import com.taweechai.realtimeopenglchart.opengl.RealtimeChartSurfaceView;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private Handler customHandler = new Handler();
	private RealtimeChartSurfaceView glChart;
	private LinearLayout glChartContainer; 
	Timer t = new Timer();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glChartContainer = (LinearLayout) findViewById(R.id.chartView);
    	glChart = new RealtimeChartSurfaceView(this);
    	glChartContainer.addView(glChart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    	glChartContainer.setVisibility(View.VISIBLE);
    	//Set the schedule function and rate
    	updateTimerThread.run();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    int j = 0;
    private Runnable updateTimerThread = new Runnable() {
    	        public void run() {
    	        	try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        		float[] f1 = new float[350];
        	        	int frequency = 10;
        	        	
        	        	for (int i = 0; i < f1.length; i++){
        	        		// Generate example signal 
        	        		f1[i] = (float)i * 1.0f*(float) Math.sin( (float)i * ((float)(2*Math.PI) *  (float)j * 1 * frequency / 44100));
        	        	}
        	        	j++;
        	        	if (j > 400){
        	        		j = 0;
        	        	}
        	        	
        	        	glChart.setChartData(f1);    	        	
    	            customHandler.postDelayed(this, 0);
    	        }
    	    };
}
