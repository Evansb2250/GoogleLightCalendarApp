package com.example.googlelightcalendar.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {
//    @Binds
//    @Singleton
   // abstract fun provideApplication(): GoogleLightCalendarApplication
}