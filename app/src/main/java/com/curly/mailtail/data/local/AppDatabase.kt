package com.curly.mailtail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.curly.mailtail.data.local.dao.NotebookDao
import com.curly.mailtail.data.local.dao.PostDao
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.data.local.entity.CommentEntity
import com.curly.mailtail.data.local.entity.ReactionEntity

@Database(
    entities = [
        NotebookEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ReactionEntity::class
    ],
    version = 3, // Поднимаем версию базы данных
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notebookDao(): NotebookDao
    abstract fun postDao(): PostDao
    // DAO для комментариев и реакций мы можем внедрить через PostDao
}