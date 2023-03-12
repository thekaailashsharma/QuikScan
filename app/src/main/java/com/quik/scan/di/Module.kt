package com.quik.scan.di

import android.app.Application
import com.quik.scan.qrcode.sensors.MeasurableSensors
import com.quik.scan.qrcode.sensors.Sensors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideLightSensor(app: Application): MeasurableSensors {
        return Sensors(app)
    }

}