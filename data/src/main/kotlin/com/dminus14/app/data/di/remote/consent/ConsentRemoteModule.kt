package com.dminus14.app.data.di.remote.consent

import com.dminus14.app.data.remote.datasource.ConsentRemoteDataSource
import com.dminus14.app.data.remote.datasource.ConsentRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsentRemoteModule {
    @Binds
    @Singleton
    abstract fun bindConsentRemoteDataSource(
        impl: ConsentRemoteDataSourceImpl,
    ): ConsentRemoteDataSource
}
