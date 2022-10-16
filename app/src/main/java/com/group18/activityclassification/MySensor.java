package com.group18.activityclassification;

import java.util.ArrayList;

/**
 * Object class for storing sensor values received from the sensors inside the mobile device
 */
public class MySensor {
    private ArrayList<Float> Acc = new ArrayList<>();
    private ArrayList<Float> LinearAcc = new ArrayList<>();
    private ArrayList<Float> Gyro = new ArrayList<>();
    private long timestamp;

    public ArrayList<Float> getAcc() {
        return Acc;
    }

    public void setAcc(ArrayList<Float> acc) {
        Acc = acc;
    }

    public ArrayList<Float> getLinearAcc() {
        return LinearAcc;
    }

    public void setLinearAcc(ArrayList<Float> linearAcc) {
        LinearAcc = linearAcc;
    }

    public ArrayList<Float> getGyro() {
        return Gyro;
    }

    public void setGyro(ArrayList<Float> gyro) {
        Gyro = gyro;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public MySensor(ArrayList<Float> A, ArrayList<Float> L, ArrayList<Float> G, long timestamp) {
        this.Acc = A;
        this.LinearAcc = L;
        this.Gyro = G;
        this.timestamp = timestamp;
    }

    public MySensor() {

    }

    public boolean isReady() {
        return this.Acc.size() == 3 && this.LinearAcc.size() == 3 && this.Gyro.size() == 3;
    }

}
