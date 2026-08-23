package com.curly.mailtail.di

import com.curly.mailtail.data.repository.MailTailRepositoryImpl
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMailTailRepository(
        repositoryImpl: MailTailRepositoryImpl
    ): MailTailRepository
}