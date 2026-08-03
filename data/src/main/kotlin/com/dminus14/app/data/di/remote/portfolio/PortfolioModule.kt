package com.dminus14.app.data.di.remote.portfolio

import com.dminus14.app.data.repository.PortfolioRepositoryImpl
import com.dminus14.app.domain.repository.PortfolioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PortfolioModule {
    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(impl: PortfolioRepositoryImpl): PortfolioRepository
}
