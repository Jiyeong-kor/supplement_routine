package com.jiyeong.supplementroutine.kmp.android.di

import android.content.Context
import com.jiyeong.supplementroutine.kmp.android.data.AndroidIntakeRecordRepository
import com.jiyeong.supplementroutine.kmp.android.data.AndroidRoutineDataStore
import com.jiyeong.supplementroutine.kmp.android.data.AndroidSettingsRepository
import com.jiyeong.supplementroutine.kmp.android.data.AndroidSupplementRepository
import com.jiyeong.supplementroutine.kmp.android.notification.AndroidNotificationPermissionController
import com.jiyeong.supplementroutine.kmp.android.notification.AndroidReminderScheduler
import com.jiyeong.supplementroutine.shared.data.IntakeRecordRepository
import com.jiyeong.supplementroutine.shared.data.SettingsRepository
import com.jiyeong.supplementroutine.shared.data.SupplementRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidAppModule {
    @Provides
    @Singleton
    fun provideRoutineDataStore(
        @ApplicationContext context: Context,
    ): AndroidRoutineDataStore = AndroidRoutineDataStore(context)

    @Provides
    @Singleton
    fun provideSupplementRepository(
        store: AndroidRoutineDataStore,
    ): SupplementRepository = AndroidSupplementRepository(store)

    @Provides
    @Singleton
    fun provideIntakeRecordRepository(
        store: AndroidRoutineDataStore,
    ): IntakeRecordRepository = AndroidIntakeRecordRepository(store)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        store: AndroidRoutineDataStore,
    ): SettingsRepository = AndroidSettingsRepository(store)

    @Provides
    @Singleton
    fun provideNotificationPermissionController(
        @ApplicationContext context: Context,
    ): AndroidNotificationPermissionController = AndroidNotificationPermissionController(context)

    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context,
        permissionController: AndroidNotificationPermissionController,
    ): AndroidReminderScheduler = AndroidReminderScheduler(context, permissionController)
}
