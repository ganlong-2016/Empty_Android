package com.empty.android.core.data.di

import com.empty.android.core.data.repository.DefaultUserSettingsRepository
import com.empty.android.core.data.repository.DemoItemRepository
import com.empty.android.core.data.repository.OfflineFirstDemoItemRepository
import com.empty.android.core.data.repository.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindDemoItemRepository(impl: OfflineFirstDemoItemRepository): DemoItemRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(impl: DefaultUserSettingsRepository): UserSettingsRepository
}
