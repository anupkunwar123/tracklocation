package com.anupkunwar.locationtracker.di

import android.app.Application
import android.content.Context
import com.anupkunwar.locationtracker.App
import com.google.android.gms.location.LocationRequest
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideApp(app: App): Application = app

    @Provides
    @Singleton
    fun provideContext(app: App): Context = app.applicationContext



    @Singleton
    @Provides
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            //request location every thirty seconds
            interval = 30 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }







}