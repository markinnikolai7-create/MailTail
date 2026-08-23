package com.curly.mailtail.di

import android.content.Context
import androidx.room.Room
import com.curly.mailtail.data.local.AppDatabase
import com.curly.mailtail.data.local.dao.NotebookDao
import com.curly.mailtail.data.local.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
// Указываем, что эти зависимости будут жить столько же, сколько живет всё приложение
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton // База данных должна быть создана ровно один раз (Паттерн Одиночка)
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mailtail_database"
        ).build()
    }

    @Provides
    fun provideNotebookDao(database: AppDatabase): NotebookDao {
        return database.notebookDao()
    }

    @Provides
    fun providePostDao(database: AppDatabase): PostDao {
        return database.postDao()
    }
}