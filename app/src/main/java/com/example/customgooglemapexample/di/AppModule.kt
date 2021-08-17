package com.example.customgooglemapexample.di

import android.content.Context
import com.example.customgooglemapexample.util.LocationTracker
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGpsBroadcastReceiver(@ApplicationContext context: Context) : GpsBroadcastReceiver {
        return GpsBroadcastReceiver(context)
    }

    @Singleton
    @Provides
    fun provideInternetBroadcastReceiver(@ApplicationContext context: Context) : InternetBroadcastReceiver {
        return InternetBroadcastReceiver(context)
    }

    @Singleton
    @Provides
    fun provideLocationTracker(@ApplicationContext context: Context) : LocationTracker {
        return LocationTracker(context)
    }
}