package com.dminus14.app.data.di.remote.dynamiclink

import com.dminus14.app.data.repository.DynamicLinkRepositoryImpl
import com.dminus14.app.domain.repository.DynamicLinkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** 동적 링크 생성(ChottuLink) Repository 구현을 domain 계약에 연결한다. */
@Module
@InstallIn(SingletonComponent::class)
abstract class DynamicLinkModule {
    @Binds
    @Singleton
    abstract fun bindDynamicLinkRepository(impl: DynamicLinkRepositoryImpl): DynamicLinkRepository
}
