package com.dminus14.app.data.di.remote.appversion

import com.dminus14.app.data.repository.AppVersionRepositoryImpl
import com.dminus14.app.domain.repository.AppVersionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppVersionModule {
    @Binds
    @Singleton
    abstract fun bindAppVersionRepository(impl: AppVersionRepositoryImpl): AppVersionRepository
}
