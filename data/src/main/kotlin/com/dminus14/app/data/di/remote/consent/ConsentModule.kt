package com.dminus14.app.data.di.remote.consent

import com.dminus14.app.data.repository.ConsentRepositoryImpl
import com.dminus14.app.domain.repository.ConsentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsentModule {
    @Binds
    @Singleton
    abstract fun bindConsentRepository(impl: ConsentRepositoryImpl): ConsentRepository
}
