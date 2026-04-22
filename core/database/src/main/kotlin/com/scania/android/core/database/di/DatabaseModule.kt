package com.scania.android.core.database.di

import android.content.Context
import androidx.room.Room
import com.scania.android.core.database.AppDatabase
import com.scania.android.core.database.dao.DemoItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "scania-android-db",
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideDemoItemDao(database: AppDatabase): DemoItemDao = database.demoItemDao()
}
