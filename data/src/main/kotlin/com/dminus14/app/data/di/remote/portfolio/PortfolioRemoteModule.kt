package com.dminus14.app.data.di.remote.portfolio

import com.dminus14.app.data.remote.datasource.PortfolioRemoteDataSource
import com.dminus14.app.data.remote.datasource.PortfolioRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PortfolioRemoteModule {
    @Binds
    @Singleton
    abstract fun bindPortfolioRemoteDataSource(
        impl: PortfolioRemoteDataSourceImpl,
    ): PortfolioRemoteDataSource
}
