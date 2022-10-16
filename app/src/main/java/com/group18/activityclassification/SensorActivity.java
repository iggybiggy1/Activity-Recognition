package com.group18.activityclassification;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

public class SensorActivity implements SensorEventListener {
    ArrayList<Float> Acc;
    ArrayList<Float> Lin;
    ArrayList<Float> Gyro;
    MySensor sensor;
    MainActivity main;

    public SensorActivity(MySensor sensor, MainActivity main) {
        this.sensor = sensor;
        this.main = main;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        switch(sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Acc = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Acc.add(sensorEvent.values[i]);
//                    System.out.println("Acc val " + sensorEvent.values[i]);
                }
                this.sensor.setAcc(Acc);
                this.sensor.setTimestamp(sensorEvent.timestamp);
                this.main.update(); // UPDATE QUEUE AND ACTIVITY EVERY TIME WE GET A NEW ENTRY FOR ACC
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                Lin = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Lin.add(sensorEvent.values[i]);
//                    System.out.println("Lin val " + sensorEvent.values[i]);
                }
                this.sensor.setLinearAcc(Lin);
                this.sensor.setTimestamp(sensorEvent.timestamp);
                break;

            case Sensor.TYPE_GYROSCOPE:
                Gyro = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Gyro.add(sensorEvent.values[i]);
//                    System.out.println("Gyro val " + sensorEvent.values[i]);
                }
                this.sensor.setGyro(Gyro);
                this.sensor.setTimestamp(sensorEvent.timestamp);
                break;
            default:
                System.out.println("Type of sensor used, was incorrect.");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
