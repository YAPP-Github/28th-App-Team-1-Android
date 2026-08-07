package com.dminus14.app.data.di.remote.appversion

import com.dminus14.app.data.remote.datasource.AppVersionRemoteDataSource
import com.dminus14.app.data.remote.datasource.AppVersionRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppVersionRemoteModule {
    @Binds
    @Singleton
    abstract fun bindAppVersionRemoteDataSource(
        impl: AppVersionRemoteDataSourceImpl,
    ): AppVersionRemoteDataSource
}
