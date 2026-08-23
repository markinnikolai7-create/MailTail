package com.curly.mailtail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.curly.mailtail.data.local.dao.NotebookDao
import com.curly.mailtail.data.local.dao.PostDao
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity

@Database(
    entities = [
        NotebookEntity::class,
        PostEntity::class
    ],
    version = 2, // Поднимаем версию с 1 на 2
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    // Описываем абстрактные функции, через которые мы будем получать доступ к SQL-запросам
    abstract fun notebookDao(): NotebookDao
    abstract fun postDao(): PostDao

}