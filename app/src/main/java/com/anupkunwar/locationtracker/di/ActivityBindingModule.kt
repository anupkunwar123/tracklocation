package com.anupkunwar.locationtracker.di

import com.anupkunwar.locationtracker.service.LocationModule
import com.anupkunwar.locationtracker.service.LocationService
import com.anupkunwar.locationtracker.ui.MainActivity
import com.anupkunwar.locationtracker.ui.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    fun contributeInjector(): MainActivity


    @ServiceScope
    @ContributesAndroidInjector(modules = [LocationModule::class])
    fun contributeInjectorLocationService(): LocationService
}

