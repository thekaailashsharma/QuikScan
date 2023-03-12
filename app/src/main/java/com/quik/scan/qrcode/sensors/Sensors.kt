package com.quik.scan.qrcode.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import com.quik.scan.qrcode.sensors.AndroidSensor

class Sensors(context: Context) : AndroidSensor(
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_LIGHT,
    sensorType = Sensor.TYPE_LIGHT
)