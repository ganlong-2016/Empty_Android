package com.empty.android.core.database.di

import android.content.Context
import androidx.room.Room
import com.empty.android.core.database.AppDatabase
import com.empty.android.core.database.dao.DemoItemDao
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
        "empty-android-db",
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideDemoItemDao(database: AppDatabase): DemoItemDao = database.demoItemDao()
}
