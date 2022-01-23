package com.daniyelp.googlemapplus.di

import android.content.Context
import com.daniyelp.locationtracker.LocationTracker
import com.daniyelp.googlemapplus.play.FakeLocationTracker
import com.example.customgooglemapexample.BuildConfig
import com.example.gpsbroadcastreceiver.GpsBroadcastReceiver
import com.example.internetbroadcastreceiver.InternetBroadcastReceiver
import com.daniyelp.openstreetmap.Osm
import com.daniyelp.snaptoroads.SnapToRoads
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
    fun provideSnapToRoads() : SnapToRoads {
        val apiKey = BuildConfig.GMAPS_API_KEY
        return SnapToRoads(apiKey)
    }

    @Singleton
    @Provides
    fun provideOsm() : Osm {
        return Osm()
    }

    @Singleton
    @Provides
    fun provideMockLocationTracker() : FakeLocationTracker {
        return FakeLocationTracker()
    }
}