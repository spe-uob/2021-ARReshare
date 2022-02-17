package com.example.ar_reshare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean initialised = false;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private float angle;

    Compass(Context context) {
        initialiseCompass(context);
    }

    public void initialiseCompass(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public double getAngleToNorth(Context context) {
        if (!initialised) initialiseCompass(context);
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        float[] output = SensorManager.getOrientation(rotationMatrix, orientationAngles);

        angle = output[0];
        // "orientationAngles" now has up-to-date information.
    }
}
