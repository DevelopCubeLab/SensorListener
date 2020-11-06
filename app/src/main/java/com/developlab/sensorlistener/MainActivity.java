package com.developlab.sensorlistener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.matrix.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    SensorManager sensorManager;
    SensorEventListener listener;

    LineChart chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        final List<Entry> list = new ArrayList<>();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null)
            return;
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Vector3 vector3 = new Vector3(event.values);
                float value = vector3.dot(new Vector3(event.values));
                Log.e(TAG, "onSensorChanged: " + value);

                if (list.size() > 2500)
                    list.clear();
                list.add(new Entry(list.size(), value));
                LineDataSet dataSet = new LineDataSet(list, "sensor data");
                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.notifyDataSetChanged();

                chart.moveViewTo(list.size(),0, YAxis.AxisDependency.RIGHT);
                chart.setVisibleXRange(0,100);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        boolean result = sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy: " + "kill sensor listener");
        //If the listener is not destroyed when the Activity is destroyed,
        //it will even monitor the data in the background, which is more terrifying.
        sensorManager.unregisterListener(listener);
    }
}
