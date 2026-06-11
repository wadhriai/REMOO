package com.remoo.app.di

import android.content.Context
import androidx.room.Room
import com.remoo.app.data.repository.DeviceDao
import com.remoo.app.data.repository.DeviceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeviceDatabase =
        Room.databaseBuilder(context, DeviceDatabase::class.java, "remoo.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDeviceDao(db: DeviceDatabase): DeviceDao = db.deviceDao()
}
