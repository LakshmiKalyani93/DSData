package com.mtuity.sensordetections;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by kalyani on 21/7/17.
 */

public class SensorsActivity extends BaseAppCompatActivity implements SensorEventListener, View.OnClickListener {

    private SensorManager sensorManager;
    private long lastUpdate;
    private Button btnStart, btnStop, btnUpload;
    private boolean started = false;
    private CopyOnWriteArrayList<AccelData> sensorData;
    private LinearLayout layout;
    private View mChart;
    private TextView updateText;
    private int drawGraph = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_graph_layout);

        btnStart = (Button) findViewById(R.id.start_btn);
        btnStop = (Button) findViewById(R.id.stop_btn);
        btnUpload = (Button) findViewById(R.id.upload_btn);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        layout = (LinearLayout) findViewById(R.id.chart_container);
        if (sensorData == null || sensorData.size() == 0) {
            btnUpload.setEnabled(false);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorData = new CopyOnWriteArrayList();
        lastUpdate = System.currentTimeMillis();

        updateText = (TextView) findViewById(R.id.update_text);
        updateText.setText("Start registering the sensor..");
        updateText.setVisibility(View.GONE);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private void getAccelerometer(SensorEvent event) {

        //if (started) {
        float[] values = event.values;
        double x = values[0];
        double y = values[1];
        double z = values[2];

        Log.i("GraphData", "XYZ  " + x + ", " + y + ", " + z);
        long timestamp = System.currentTimeMillis();
        AccelData data = new AccelData(timestamp, x, y, z);
        sensorData.add(data);
        new DrawGraph().execute(sensorData);

    }

    private class DrawGraph extends AsyncTask<CopyOnWriteArrayList<AccelData>, Void, ChartModel> {

        @Override
        protected ChartModel doInBackground(CopyOnWriteArrayList<AccelData>... params) {
            ChartModel model = openChart(params[0]);
            return model;
        }


        @Override
        protected void onPostExecute(ChartModel result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            if (result != null) {
                Log.i("dataset ", "DataSet: " + result.getDataset().toString());
                Log.i("getMultiRenderer ", "getMultiRenderer DataSet :" + result.getMultiRenderer().toString());
                mChart = ChartFactory.getLineChartView(getBaseContext(), result.getDataset(),
                        result.getMultiRenderer());
//
                layout.removeAllViews();
                // Adding the Line Chart to the LinearLayout
                layout.addView(mChart);
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private ChartModel openChart(CopyOnWriteArrayList<AccelData> list) {
        CopyOnWriteArrayList<AccelData> params = new CopyOnWriteArrayList<>(list);
        try {
            if (params != null || params.size() > 0) {
                long t = params.get(0).getTimestamp();
                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

                XYSeries xSeries = new XYSeries("X");
                XYSeries ySeries = new XYSeries("Y");
                XYSeries zSeries = new XYSeries("Z");

//            for(Iterator<AccelData> it = sensorData.iterator(); it.hasNext();) {
//                AccelData data = it.next();
//                xSeries.add(data.getTimestamp() - t, data.getX());
//                ySeries.add(data.getTimestamp() - t, data.getY());
//                zSeries.add(data.getTimestamp() - t, data.getZ());
//            }

//
                for (AccelData data : params) {
                    xSeries.add(data.getTimestamp() - t, data.getX());
                    ySeries.add(data.getTimestamp() - t, data.getY());
                    zSeries.add(data.getTimestamp() - t, data.getZ());
                }

                dataset.addSeries(xSeries);
                dataset.addSeries(ySeries);
                dataset.addSeries(zSeries);

                XYSeriesRenderer xRenderer = new XYSeriesRenderer();
                xRenderer.setColor(Color.RED);
                xRenderer.setPointStyle(PointStyle.CIRCLE);
                xRenderer.setFillPoints(true);
                xRenderer.setLineWidth(1);
                xRenderer.setAnnotationsTextSize(50);
                xRenderer.setDisplayChartValues(false);
                xRenderer.setDisplayChartValuesDistance(5);

                XYSeriesRenderer yRenderer = new XYSeriesRenderer();
                yRenderer.setColor(Color.GREEN);
                yRenderer.setPointStyle(PointStyle.CIRCLE);
                yRenderer.setFillPoints(true);
                yRenderer.setLineWidth(1);
                yRenderer.setAnnotationsTextSize(50);
                yRenderer.setDisplayChartValues(false);
                yRenderer.setDisplayChartValuesDistance(5);


                XYSeriesRenderer zRenderer = new XYSeriesRenderer();
                zRenderer.setColor(Color.BLUE);
                zRenderer.setPointStyle(PointStyle.CIRCLE);
                zRenderer.setFillPoints(true);
                zRenderer.setLineWidth(1);
                zRenderer.setAnnotationsTextSize(50);
                zRenderer.setDisplayChartValues(false);
                zRenderer.setDisplayChartValuesDistance(5);


                XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
                multiRenderer.setXLabels(0);
                multiRenderer.setLabelsColor(Color.RED);
                multiRenderer.setChartTitle("t vs (x,y,z)");
                multiRenderer.setAxisTitleTextSize(40);
                multiRenderer.setChartTitleTextSize(40);
                multiRenderer.setXTitle("Sensor Data");
                multiRenderer.setYTitle("Values of Acceleration");
                multiRenderer.setZoomButtonsVisible(false);
                for (int i = 0; i < params.size(); i++) {
                    multiRenderer.addXTextLabel(i + 1, ""
                            + (params.get(i).getTimestamp() - t));
                }
                for (int i = 0; i < 12; i++) {
                    multiRenderer.addYTextLabel(i + 1, "" + i);
                }

                multiRenderer.addSeriesRenderer(xRenderer);
                multiRenderer.addSeriesRenderer(yRenderer);
                multiRenderer.addSeriesRenderer(zRenderer);

                // Getting a reference to LinearLayout of the MainActivity Layout

                ChartModel model = new ChartModel();
                model.setDataset(dataset);
                model.setMultiRenderer(multiRenderer);

                return model;

                // Creating a Line Chart
//            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
//                    multiRenderer);
////
//            // Adding the Line Chart to the LinearLayout
//            layout.addView(mChart);

            }
        } catch (ConcurrentModificationException e) {

        }
        return new ChartModel();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);

        Sensor accel = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        // unregister listener
        // sensorManager.unregisterListener(this);
        super.onPause();
//        if (started == true) {
//            sensorManager.unregisterListener(this);
//        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start_btn:
                // showDialog();
                updateText.setText("Stop it to read the SensorData");
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnUpload.setEnabled(false);
                // save prev data if available
                started = true;
                sensorData = new CopyOnWriteArrayList();
                Sensor accel = sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accel,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case R.id.stop_btn:
                //dismissDialog();
                updateText.setVisibility(View.GONE);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnUpload.setEnabled(true);
                started = false;
                sensorManager.unregisterListener(this);
                layout.removeAllViews();
                openChart(sensorData);

                // show data in chart
                break;
            case R.id.upload_btn:
                break;
            default:
                break;
        }
    }

}
