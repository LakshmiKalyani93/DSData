package com.mtuity.sensordetections;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mtuity.sensordetections.network.NetworkUtil;
import com.mtuity.sensordetections.reversegeocoding.Features;
import com.mtuity.sensordetections.reversegeocoding.SpaceAddressData;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by kalyani on 21/7/17.
 */

public class SensorsActivity extends BaseAppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final int PERMISSIONS_LOCATION = 100;
    private static final int EMAIL_REQUEST_CODE = 101;
    private SensorManager sensorManager;
    private long lastUpdate;
    private Button btnStart, btnStop, btnUpload;
    private boolean started = false;
    private CopyOnWriteArrayList<AccelData> sensorData;
    private LinearLayout layout;
    private View mChart;
    private TextView updateText;
    private int drawGraph = 0;
    private LocationManager locationManager;
    private double currentLatitude;
    private double currentLongitude;
    public static final String MAP_ACCESS_TOKEN = "pk.eyJ1IjoiYW5pdGhhIiwiYSI6ImNpandwZzVsZzBtd3d2Mm01czFvZ2hnb24ifQ.GKDUFD8mPIQwwOALNveP5g";
    private boolean checkGPS;
    private boolean checkNetwork;
    private boolean isNetworkEnabled;
    //"pk.eyJ1IjoiZGhhcm1hc2FpIiwiYSI6ImNpaThtOHk0aTAwaWFzem0zODFlaG5mcWcifQ.dYfawnh8JbC-TiqBxap6jQ";//pk.eyJ1IjoibWFwYm94IiwiYSI6IlhHVkZmaW8ifQ.hAMX5hSW-QnTeRCMAy9A8Q


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_graph_layout);
        initiateRequestPermissions();

        initiViews();


    }

    private void initiViews() {
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
        updateText.setText("Start registering the sensor to collect the data");
        updateText.setVisibility(View.VISIBLE);
    }

    private void initiateRequestPermissions() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_LOCATION);
            return;
        }

        GPSTracker gpsTracker = new GPSTracker(this);
        currentLatitude = gpsTracker.getLatitude();
        currentLongitude = gpsTracker.getLongitude();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do nothing
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Email Sent Successfully", Toast.LENGTH_LONG).show();
        }
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
        // new DrawGraph().execute(sensorData);

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

    private void callServiceOfReverseCoding(LatLng spaceInfo) {
        SensorServices addressApi = RestClient.getInstance().getSearchService(this);
        String longAndLats = spaceInfo.longitude + "," + spaceInfo.latitude;

        if (NetworkUtil.isOnline(this)) {
            showDialog();
            startServiceOfGeoCoding(addressApi, longAndLats);
        } else {
            Toast.makeText(this, "Unable to connect, please check your connection.", Toast.LENGTH_LONG).show();
        }

    }

    private void startServiceOfGeoCoding(SensorServices addressApi, String longAndLats) {
        addressApi.getAddressByCoordinates(longAndLats, MAP_ACCESS_TOKEN, new retrofit.Callback<SpaceAddressData>() {
            @Override
            public void success(SpaceAddressData spaceAddressData, Response response) {
                successInGeoCoding(spaceAddressData);
            }

            @Override
            public void failure(RetrofitError error) {
                dismissDialog();
            }
        });
    }

    private void successInGeoCoding(SpaceAddressData spaceAddressData) {
        if (spaceAddressData != null) {
            Features[] features = spaceAddressData.getFeatures();
            if (features != null && features.length > 0) {
                String placeName = features[0].getPlaceName();
                dismissDialog();
                uploadCSVFile(placeName);
            }
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

//        Sensor accel = sensorManager
//                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(this, accel,
//                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        // unregister listener
        // sensorManager.unregisterListener(this);
        super.onPause();
        if (started == true) {
            sensorManager.unregisterListener(this);
        }
        //sensorManager.unregisterListener(this);
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
                //openChart(sensorData);
                prepareCSVFile();
                new DrawGraph().execute(sensorData);
                // show data in chart
                break;
            case R.id.upload_btn:
                //uploadCSVFile();
                GPSTracker gpsTracker = new GPSTracker(this);
                currentLatitude = gpsTracker.getLatitude();
                currentLongitude = gpsTracker.getLongitude();
                callServiceOfReverseCoding(new LatLng(currentLatitude, currentLongitude));
                break;
            default:
                break;
        }
    }

    private void uploadCSVFile(String placeName) {

        File externalStorageDir = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDir, "/SensorData/data.csv");

        if (file.exists()) {
            Uri u1 = Uri.fromFile(file);

            String msg = "Located at address: " + placeName + "\n LatLng Positions: " + currentLatitude + "," + currentLongitude;

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Accelerometer Sensor Readings File");
            sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
            sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/html");
            //startActivity(sendIntent);
            startActivityForResult(sendIntent, EMAIL_REQUEST_CODE);
        } else {
            Toast.makeText(this, "The requested file is not found", Toast.LENGTH_LONG).show();
        }
    }

    private void prepareCSVFile() {

        GPSTracker gpsTracker = new GPSTracker(this);
        String columnString = "TimeStamp,X-Value,Y-Value,Z-Value,Latitude,Longitude";

        StringBuilder builder = new StringBuilder();

        for (AccelData obj : sensorData) {
            currentLatitude = gpsTracker.getLatitude();
            currentLongitude = gpsTracker.getLongitude();
            builder.append(obj.getTimestamp() + "," + obj.getX() + ","
                    + obj.getY() + "," + obj.getZ() + "," + currentLatitude + "," + currentLongitude + "\n");
        }
        String dataString = builder.toString();
        //String dataString = "\"" + currentUser + "\",\"" + currentUser + "\",\"" + currentUser + "\"";
        String combinedString = columnString + "\n" + dataString;

        File file = null;
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
            File dir = new File(root.getAbsolutePath() + "/SensorData");
            dir.mkdirs();
            file = new File(dir, "data.csv");
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.write(combinedString.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
