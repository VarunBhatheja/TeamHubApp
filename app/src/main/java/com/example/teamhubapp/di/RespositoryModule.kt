package com.example.teamhubapp.di

import com.example.teamhubapp.feature_users.data.repository.UserRepositoryImpl
import com.example.teamhubapp.feature_users.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// Binds interface to implementation
// Anywhere UserRepository is needed → UserRepositoryImpl is provided

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}

