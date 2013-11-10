package com.taweechai.realtimeopenglchart.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class RealtimeChartRenderer implements Renderer {

	private SignalChart sineChart;
	GL10 glx;
	public volatile float[] chartData = new float[400];
	int width;
	int height;
	Context context;
    /** Constructor */
    public RealtimeChartRenderer(Context context) {
        this.sineChart = new SignalChart();
        this.context = context;
    }

	@Override
	public void onDrawFrame(GL10 gl) {
		// clear Screen and Depth Buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //gl.glViewport(0, 0, this.width, this.height);
        // Reset the Modelview Matrix
        gl.glLoadIdentity();
        // Drawing
        //Log.d("Chart Ratio1 "," width " +width + " H " + height);
        //gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, -3.0f);     // move 5 units INTO the screen
                                                // is the same as moving the camera 5 units away
        this.sineChart.setResolution(width, height);
        this.sineChart.setChartData(chartData);
        sineChart.draw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
	    this.height = height;
	    
		if(height == 0) {                       //Prevent A Divide By Zero By
            height = 1;                         //Making Height Equal One
        }
        gl.glViewport(0, 0, width, height);     //Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);    //Select The Projection Matrix
        gl.glLoadIdentity();                    //Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        //Log.d("Chart Ratio2 "," width " +width + " H " + height);
        GLU.gluPerspective(gl, 45.0f, (float) height * 6.0f/(float)width, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);     //Select The Modelview Matrix
        gl.glLoadIdentity();                    //Reset The Modelview Matrix
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
	}
}

class SignalChart {
	public float chartData[] = new float[350];
	private float CHART_POINT = 350.0f;
	int width;
	int height;
	private FloatBuffer vertexBuffer;   // buffer holding the vertices
	/*private float vertices[] = {
						-1.0f, -0.2f,  0.0f,
			            -0.4f, 0.2f,  0.0f,        // V1 - first vertex (x,y,z)
			             -0.3f, -0.2f,  0.0f,        // V2 - second vertex
			             -0.2f,  0.2f,  0.0f ,        // V3 - third vertex
			             -0.1f, -0.2f,  0.0f,        // V2 - second vertex
			             0.0f,  -0.0f,  0.0f  ,       // V3 - third vertex
			             0.1f, 0.2f,  0.0f,        // V2 - second vertex
			             0.2f,  -0.2f,  0.0f,         // V3 - third vertex
			             0.3f, 0.2f,  0.0f,        // V2 - second vertex
			             0.4f,  -0.2f,  0.0f,         // V3 - third vertex
			             1.0f,  0.2f,  0.0f
			    };*/
	private float vertices[] = new float[(int) (CHART_POINT * 3.0f)];
	
	public void drawRealtimeChart (){
		float verticeInc = 2.0f/CHART_POINT;			
		// update x vertrices
		for(int i = 0; i < CHART_POINT * 3; i = i + 3) {
			if ( i < CHART_POINT * 3){
				vertices[i] = -1 + (i * verticeInc)/3;
			}
		}
		// update y vertrices
		int k = 0;
		for(int i = 1; i < CHART_POINT * 3; i = i + 3) {
			if ( i < CHART_POINT * 3){
				//vertices[i] = 1.0f*(float) Math.sin( (float)i * ((float)(2*Math.PI) * 1 * frequency / 44100));
				vertices[i] = chartData[k];
				k++;
			}
		}
		// update z vertrices
		for(int i = 2; i < CHART_POINT * 3; i = i + 3) {
			if ( i + 3 < CHART_POINT * 3){
				vertices[i] = 0.0f;
			}
		}
		// Debug Chart Value
		/*
		for (int i = 0; i < CHART_POINT * 3; i++){
			Log.d("VERTICES", "test :" + vertices[i]);
		}*/
	}

	/**
	 * @param chartData the chartData to set
	 */
	public void setChartData(float[] chartData) {
		this.chartData = chartData;
		drawRealtimeChart();
		vertexGenerate();
	}

	public SignalChart() {
		drawRealtimeChart();
		vertexGenerate();
    }
	
	public void vertexGenerate(){
		// a float has 4 bytes so we allocate for each coordinate 4 bytes
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        // allocates the memory from the byte buffer
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        // fill the vertexBuffer with the vertices
        vertexBuffer.put(vertices);		         
        // set the cursor position to the beginning of the buffer
        vertexBuffer.position(0);
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	    this.width = width;
	    this.height = height;
	}
	public void setResolution(int width, int height){
		this.width = width;
	    this.height = height;
	}
	
	public void draw(GL10 gl) {
		//Log.d("Chart Ratio3 "," width " +width + " H " + height);
		gl.glViewport(0, 0, width, height);
		// bind the previously generated texture
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// set the color for the triangle
		gl.glColor4f(0.0f, 0.1f, 0.1f, 0.2f);
		//gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		// Draw the vertices as triangle strip
		//gl.glDrawElements(GL10.GL_LINE_LOOP, vertices.length/3, GL10.GL_UNSIGNED_SHORT, vertexBuffer);
		// Draw the vertices as triangle strip
		gl.glLineWidth(3.0f);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertices.length/3);
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
