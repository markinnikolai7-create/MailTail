package com.curly.mailtail.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = NotebookEntity::class, // С какой таблицей связываем
            parentColumns = ["id"],         // Колонка в родительской таблице (NotebookEntity)
            childColumns = ["notebook_id"], // Колонка в этой таблице (PostEntity)
            onDelete = ForeignKey.CASCADE   // Правило: если удален блокнот, удаляем и пост
        )
    ]
)
data class PostEntity(
    @PrimaryKey val id: String, // Уникальный ID поста

    // Аннотация @ColumnInfo(index = true) ускоряет поиск постов по ID блокнота
    @ColumnInfo(name = "notebook_id", index = true) val notebookId: String,

    val authorName: String,
    val content: String,
    val dateMillis: Long, // Храним время в миллисекундах (Long), это оптимальнее для БД

    val isDraft: Boolean = false,
    val isSyncing: Boolean = false // Флаг: отправлен пост на сервер или еще в очереди
)