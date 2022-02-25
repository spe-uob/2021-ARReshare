package com.example.ar_reshare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magneticField;
    private boolean initialised = false;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    // The latest angle to the north of the user's device
    private float angle;

    // To use compass anywhere, initialise it and pass it the current context
    Compass(Context context) {
        initialiseCompass(context);
    }

    private void initialiseCompass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // You can call this method to get the latest recorder angle to north
    public double getAngleToNorth() {
        updateOrientationAngles();
        return angle;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
    }

    // We do not need to consider the accuracy of the sensors
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateOrientationAngles() {
        // Read the data from the sensor into the accelerometer and magnetometer stored readings
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        float[] output = SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // Assign the current angle to the north as an attribute of the class
        angle = output[0];
    }
}
