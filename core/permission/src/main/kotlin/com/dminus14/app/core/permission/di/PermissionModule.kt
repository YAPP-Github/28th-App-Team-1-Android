package com.dminus14.app.core.permission.di

import com.dminus14.app.core.permission.DefaultPermissionManager
import com.dminus14.app.core.permission.PermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PermissionModule {
    @Binds
    @Singleton
    abstract fun bindPermissionManager(impl: DefaultPermissionManager): PermissionManager
}
