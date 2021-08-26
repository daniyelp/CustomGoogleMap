package com.example.customgooglemapexample.di

import android.content.Context
import com.example.customgooglemapexample.R
import com.example.customgooglemapexample.util.LocationTracker
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import com.example.openstreetmap.Osm
import com.example.snap_to_roads.SnapToRoads
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

    @Singleton
    @Provides
    fun provideSnapToRoads(@ApplicationContext context: Context) : SnapToRoads{
        val apiKey = context.resources.getString(R.string.google_maps_api_key)
        return SnapToRoads(apiKey)
    }

    @Singleton
    @Provides
    fun provideOsm() : Osm {
        return Osm()
    }

    //below is very bad design
    /*@Singleton
    @Provides
    fun provideApiKey(@ApplicationContext context: Context) : String {
        return context.resources.getString(R.string.google_maps_api_key)
    }*/



}