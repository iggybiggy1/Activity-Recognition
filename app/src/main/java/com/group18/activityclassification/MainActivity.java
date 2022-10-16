package com.group18.activityclassification;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private MySensor mySensor;
    private Classifier cls;
    private InputStream fileStream;
    private Queue queue;
    private TextView currentActivity;
    private final ArrayList<String> previousValues = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    // FILES
    private final static String FILE_J48 = "j48tree.model";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ListView listView = findViewById(R.id.list_view);
        this.arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, previousValues);
        listView.setAdapter(arrayAdapter);
        currentActivity = findViewById(R.id.textView2);

        init();
    }

    /**
     * This function initializes this instance with a new queue, classifier and sensors.
     */
    private void init() {
        try {
            initClassifier(FILE_J48);
            this.queue = new Queue();

            // Setup sensors
            mySensor = new MySensor();
            SensorEventListener sensorListener = new SensorActivity(mySensor, this);
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 20000);
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 20000);
            sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 20000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function initializes the classifier of the model from the file.
     * @param file file name to be used, from the assets folder
     * @throws Exception
     */
    private void initClassifier(String file) throws Exception {
        this.fileStream = getAssets().open(file);
        this.cls = (Classifier) weka.core.SerializationHelper
                .read(this.fileStream);
    }

    /**
     * This function closes the input file streams
     * @throws IOException
     */
    private void closeStream() throws IOException {
        if (this.fileStream != null) this.fileStream.close();

    }

    /**
     * This function is called to update the user activity.
     * This function adds the activity to the queue and tallies the queue when it is ready for the final decision on the user activity.
     */
    public void update() {
        this.queue.addToQueue(this.getActivityRightPocket());
        if (this.queue.isReady()) {
            Attribute activity = this.queue.tallyQueue();

            if (previousValues.size() > 0 && previousValues.get(0).contains(activity.toString())) return;

            // Get current timestamp
            Date date = new Date(System.currentTimeMillis());
            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            String dateFormatted = formatter.format(date);

            // Add
            currentActivity.setText(activity.toString());
            previousValues.add(0, dateFormatted + ' ' + this.queue.tallyQueue().toString());
            this.arrayAdapter.notifyDataSetChanged();

            // DEVELOPER DEBUG PURPOSE
            System.out.println("[SYSTEM] Activity detected: " + this.queue.tallyQueue());
        }
    }

    /**
     * This function uses the model and the sensor data to predict the activity of the user.
     * @return Attribute with the activity of the user
     */
    private Attribute getActivityRightPocket() {
        if (!this.mySensor.isReady()) return null;

        // Attributes for the prediction model
        // Right pocket
        final weka.core.Attribute attributeRightPocketAx = new weka.core.Attribute(Attribute.RIGHT_POCKET_AX.toString());
        final weka.core.Attribute attributeRightPocketAy = new weka.core.Attribute(Attribute.RIGHT_POCKET_AY.toString());
        final weka.core.Attribute attributeRightPocketAz = new weka.core.Attribute(Attribute.RIGHT_POCKET_AZ.toString());
        final weka.core.Attribute attributeRightPocketLx = new weka.core.Attribute(Attribute.RIGHT_POCKET_LX.toString());
        final weka.core.Attribute attributeRightPocketLy = new weka.core.Attribute(Attribute.RIGHT_POCKET_LY.toString());
        final weka.core.Attribute attributeRightPocketLz = new weka.core.Attribute(Attribute.RIGHT_POCKET_LZ.toString());
        final weka.core.Attribute attributeRightPocketGx = new weka.core.Attribute(Attribute.RIGHT_POCKET_GX.toString());
        final weka.core.Attribute attributeRightPocketGy = new weka.core.Attribute(Attribute.RIGHT_POCKET_GY.toString());
        final weka.core.Attribute attributeRightPocketGz = new weka.core.Attribute(Attribute.RIGHT_POCKET_GZ.toString());

        final List<String> classes = new ArrayList<String>() {
            {
                add(Attribute.WALKING.toString());
                add(Attribute.STANDING.toString());
                add(Attribute.JOGGING.toString());
                add(Attribute.SITTING.toString());
                add(Attribute.BIKING.toString());
                add(Attribute.UPSTAIRS.toString());
                add(Attribute.DOWNSTAIRS.toString());
            }
        };

        // Instances(...) requires ArrayList<> instead of List<>...
        ArrayList<weka.core.Attribute> attributeListRightPocket = new ArrayList<weka.core.Attribute>(2) {
            {
                add(attributeRightPocketAx);
                add(attributeRightPocketAy);
                add(attributeRightPocketAz);
                add(attributeRightPocketLx);
                add(attributeRightPocketLy);
                add(attributeRightPocketLz);
                add(attributeRightPocketGx);
                add(attributeRightPocketGy);
                add(attributeRightPocketGz);
                weka.core.Attribute attributeClass = new weka.core.Attribute("@@class@@", classes);
                add(attributeClass);
            }
        };

        // unpredicted data sets (reference to sample structure for new instances)
        Instances dataUnpredicted = new Instances("TestInstances",
                attributeListRightPocket, 1);
        // last feature is target variable
        dataUnpredicted.setClassIndex(dataUnpredicted.numAttributes() - 1);

        DenseInstance instanceRightPocket = new DenseInstance(dataUnpredicted.numAttributes()) {
            {
                setValue(attributeRightPocketAx, mySensor.getAcc().get(0));
                setValue(attributeRightPocketAy, mySensor.getAcc().get(1));
                setValue(attributeRightPocketAz, mySensor.getAcc().get(2));
                setValue(attributeRightPocketLx, mySensor.getLinearAcc().get(0));
                setValue(attributeRightPocketLy, mySensor.getLinearAcc().get(1));
                setValue(attributeRightPocketLz, mySensor.getLinearAcc().get(2));
                setValue(attributeRightPocketGx, mySensor.getGyro().get(0));
                setValue(attributeRightPocketGy, mySensor.getGyro().get(1));
                setValue(attributeRightPocketGz, mySensor.getGyro().get(2));
            }
        };

        // instance to use in prediction
        instanceRightPocket.setDataset(dataUnpredicted);

        // predict new sample
        try {
            double result = cls.classifyInstance(instanceRightPocket);
            return Attribute.valueOf(classes.get(Double.valueOf(result).intValue()).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}