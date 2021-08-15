package com.example.customgooglemapexample.di

import android.content.Context
import com.example.customgooglemapexample.util.GpsBroadcastReceiver
import com.example.customgooglemapexample.util.LocationTracker
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
    fun provideLocationTracker(@ApplicationContext context: Context) : LocationTracker {
        return LocationTracker(context)
    }
}