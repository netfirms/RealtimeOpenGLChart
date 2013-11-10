package com.taweechai.realtimeopenglchart.opengl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class RealtimeChartSurfaceView extends GLSurfaceView{

	private RealtimeChartRenderer chartRenderer;	
	private float[] datapoints = new float[350];
    private float gMaxValue = 0f;
    private float gMinValue = 0f;
        
    int width;
	int height;
	
	boolean isUpdating = false;
	int i = 1;
	
	public RealtimeChartSurfaceView(Context context) {
		super(context);
            
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.setZOrderOnTop(true); //necessary
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // Set the Renderer for drawing on the GLSurfaceView
        chartRenderer = new RealtimeChartRenderer(context);
        setRenderer(chartRenderer);
        for (int i = 0; i < datapoints.length; i++){
        	datapoints[i] = 0;
        }
        setChartData(datapoints);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        new Thread(new Task()).start();
	}
	
	public void setChartData(float[] datapoints) {
    	if (datapoints.length > 0){
    		isUpdating = true;
    		this.datapoints = datapoints.clone();
    		gMaxValue = getMax(datapoints);
        	gMinValue = getMin(datapoints);
        	//this.datapoints[0] = (((0.0f - gMinValue) * (1.0f - (-1.0f))/(gMaxValue - gMinValue)) + (-1));
    		for (int i = 0; i < this.datapoints.length; i++){
    			this.datapoints[i] = (((datapoints[i] - gMinValue) * (1.0f - (-1.0f))/(gMaxValue - gMinValue)) + (-1));
    			//Log.d("DD", "Data Chart" + this.datapoints[i]);
    		}
    		//this.datapoints[this.datapoints.length - 1] = (((0.0f - gMinValue) * (1.0f - (-1.0f))/(gMaxValue - gMinValue)) + (-1));
    		isUpdating = false;
    	}
    	else {
    		
    	}
    }
	
	class Task implements Runnable {
        @Override
        public void run() {
        	while (true){
        		if (!isUpdating){
        			chartRenderer.chartData = datapoints;
            		requestRender();
        		}
        		
        		try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        	}
        }
    }
	
	private float getMax(float[] array) {
    	if (array.length > 0){
    		float max = array[0];
            for (int i = 1; i < array.length; i++) {
                if (array[i] > max) {
                    max = array[i];
                }
            }
            return max;
    	}
    	else {
    		return 0f;
    	}
    }
    
    private float getMin(float[] array) {
    	if (array.length > 0){
    		float min = array[0];
    		// not use 0.00 as minimum point
            for (int i = 1; i < array.length; i++) {
            	if (array[i] > 0.0f){
                		min = array[i];
                }
            }
            // finding minimum point
            for (int i = 1; i < array.length; i++) {
        		if ((array[i] < min) && (array[i] > 0)) {
            		min = array[i];
            	}
            }
            return min;
    	}
    	else {
    		return 0f;
    	}
        
    }
    
    public boolean compareArrays(float[] array1, float[] array2) {
        boolean b = true;
        if (array1 != null && array2 != null){
          if (array1.length != array2.length)
              b = false;
          else
              for (int i = 0; i < array2.length; i++) {
                  if (array2[i] != array1[i]) {
                      b = false;    
                  }                 
            }
        }else{
          b = false;
        }
        return b;
    }

}
